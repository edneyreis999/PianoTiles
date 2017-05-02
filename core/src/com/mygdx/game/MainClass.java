package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;

import java.util.Random;

import static com.mygdx.game.Constantes.*;

public class MainClass extends ApplicationAdapter {

	private ShapeRenderer shapeRenderer;
	private Array<Fileira> fileiras;
	private SpriteBatch batch;
	private Texture texIniciar;
	private Piano piano;

	private float tempoTotal;
	private int indexInferior;
	private int pontos;

	private BitmapFont fonte;
	private GlyphLayout glyphLayout;

	private Random random;

	private int estado;

	@Override
	public void create () {
		shapeRenderer = new ShapeRenderer();
		shapeRenderer.setAutoShapeType(true);
		batch = new SpriteBatch();
		texIniciar = new Texture("iniciar.png");
		random = new Random();
		piano = new Piano("natal");
		glyphLayout = new GlyphLayout();

		fileiras = new Array<Fileira>();

		FreeTypeFontGenerator.setMaxTextureSize(2048);
		FreeTypeFontGenerator geradorFontes = new FreeTypeFontGenerator(Gdx.files.internal("font.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.size = (int) (0.08f * screeny);
		parameter.color = Color.GRAY;

		fonte = geradorFontes.generateFont(parameter);

		geradorFontes.dispose();
		iniciar();
	}

	@Override
	public void render () {
		input();


		update(Gdx.graphics.getDeltaTime());
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		shapeRenderer.begin();

		for (Fileira fila:fileiras) {
			fila.draw(shapeRenderer);
		}

		shapeRenderer.end();

		batch.begin();

		if(estado == 0){
			batch.draw(texIniciar, 0, tileHeight /4, screenx, tileHeight /2);
		}

		fonte.draw(batch, String.valueOf(pontos), 0, screeny);
		fonte.draw(batch, String.format("%.2f", velAtual/tileHeight), screenx - calculaLarguraTexto(fonte, String.format("%.2f", velAtual/tileHeight)), screeny);
		batch.end();

	}

	private void input() {
		if(Gdx.input.justTouched()){
			int x = Gdx.input.getX();
			// lembrando que no touch, a coordenada de Y é invertida!
			int y = screeny - Gdx.input.getY();
			if(estado == 0){
				estado = 1;
			}
			if(estado == 1){
				for (int i = 0; i < fileiras.size; i++) {
					int retorno = fileiras.get(i).toque(x, y);
					if(retorno != 0){
						// tocou na tile certa e na fileira certa
						if(retorno == 1 && i == indexInferior){
							//continuar
							pontos ++;
							indexInferior++;
							piano.tocar();
						}else if(retorno == 1){
							//finalizar da forma 1
							fileiras.get(indexInferior).erro();
							finalizar(0);
						}else{
							//finalizar da forma 2
							finalizar(0);
						}

						break;
					}
				}
			}else if(estado == 2){
				iniciar();
			}
		}
	}

	private void finalizar(int opcao) {
		Gdx.input.vibrate(200);
		estado = 2;
		if(opcao == 1){
			for(Fileira f:fileiras){
				f.y += tileHeight;
			}
		}
	}


	private void update(float deltaTime) {
		if(estado == 1){
			tempoTotal = tempoTotal + deltaTime;

			// 8 fica com para adicionar na velocidade do game
			velAtual = velIni + tileHeight*tempoTotal/8f;

			for (int i = 0; i < fileiras.size; i++) {
				int retorno = fileiras.get(i).update(deltaTime);
				fileiras.get(i).anim(deltaTime);
				if(retorno != 0){
					if(retorno == 1){
						fileiras.removeIndex(i);
						i--;
						indexInferior --;
						adicionar();
					}else if(retorno == 2){
						finalizar(1);
					}
				}
			}
		}else if(estado == 2){
			for (Fileira f: fileiras) {
				f.anim(deltaTime);
			}
		}
	}

	private void adicionar() {
		float y = fileiras.get(fileiras.size -1).y + tileHeight;
		fileiras.add(new Fileira(y, random.nextInt(4)));
	}

	private void iniciar(){
		tempoTotal = 0;
		indexInferior = 0;
		pontos = 0;
		estado = 0;
		velAtual = 0;

		fileiras.clear();
		fileiras.add(new Fileira(tileHeight, random.nextInt(4)));

		adicionar();
		adicionar();
		adicionar();
		adicionar();

		piano.reset();
	}

	private float calculaLarguraTexto(BitmapFont font, String texto){
		glyphLayout.reset();
		glyphLayout.setText(font, texto);
		return glyphLayout.width;
	}

	@Override
	public void dispose () {

		shapeRenderer.dispose();
		batch.dispose();
		texIniciar.dispose();
		piano.dispose();
	}
}
