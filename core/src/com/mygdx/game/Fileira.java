package com.mygdx.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import static com.mygdx.game.Constantes.*;

/**
 * Created by Desktop on 27/04/2017.
 */

public class Fileira {
    public float y;
    private int correta;
    private int pos;
    private boolean ok;
    private boolean destruido;
    private float anim;

    public Fileira(float y, int correta){
        this.y = y;
        this.correta = correta;
        ok = false;
        destruido = false;
        anim = 0;
    }

    public void draw(ShapeRenderer shapeRenderer){
        shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(verde);

        // Desenha o tile correto ( o pintado ) na posição correta
        shapeRenderer.rect(correta * tileWidth, y, tileWidth, tileHeight);

        if(destruido){
            if(ok){
                shapeRenderer.setColor(certo);
            }else{
                shapeRenderer.setColor(errado);
            }

            shapeRenderer.rect(pos*tileWidth + (tileWidth - anim*tileWidth)/2f,
                    y + (tileHeight - anim*tileHeight)/2,
                    anim*tileWidth, anim*tileHeight);
        }

        // Pinta ele com uma borda cinza
        shapeRenderer.set(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.GRAY);
        // coloca uma borda cinza em volta dos tile
        for (int i = 0; i < 4; i++) {
            shapeRenderer.rect(i * tileWidth, y, tileWidth, tileHeight);
        }
    }

    public void anim(float time){
        if(destruido && anim < 1){
            anim += 5*time;
            if(anim >= 1){
                anim = 1;
            }
        }
    }

    public int update(float time){
        //Delta S = Delta T * Velocidade :)
        y = y - time * velAtual;

        if(y < 0 - tileHeight){
            if(ok){
                return 1;
            }else{
                erro();
                return 2;
            }
        }
        return 0;
    }

    /**
     *
     * @param tx coordenada x do toque
     * @param ty coordenada y do toque
     * @return 0 para click fora da fileira;
     * 1 para click correto
     * 2 para click errado
     */
    public int toque(int tx, int ty) {
        // o toque em Y está dentro da fileira?
        if(ty >= y && ty <= y + tileHeight){
            pos = tx / tileWidth;
            if(pos == correta){
                ok = true;
                destruido = true;
                return 1;
            }else{
                ok = false;
                destruido = true;
                return 2;
            }

        }
        return 0;
    }

    public void erro(){
        destruido = true;
        pos = correta;
    }
}
