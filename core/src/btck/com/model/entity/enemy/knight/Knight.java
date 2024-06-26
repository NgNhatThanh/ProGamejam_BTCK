package btck.com.model.entity.enemy.knight;

import btck.com.MyGdxGame;
import btck.com.common.GameManager;
import btck.com.controller.attack.DEAL_DAMAGE_TIME;
import btck.com.common.Constants;
import btck.com.model.entity.Enemy;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import static java.lang.Math.abs;
import static java.lang.Math.sqrt;

public class Knight extends Enemy {

    static TextureAtlas atlas = new TextureAtlas(Gdx.files.internal(Constants.KNIGHT_ATLAS_PATH));

    public Knight(){
        super();
        FRAME_DURATION = Constants.FRAME_DURATION[0];

        attackRange = 130;
        currentHealth = 6 + bonusHealth;
        exp = 7;

        normalSpeed = 100;
        currentSpeed = 100;
        animations = new Animation[5];

        hitbox = new Rectangle(0, 0, width, height);

        animations[0] = new Animation<>(FRAME_DURATION, atlas.findRegions("spr-spawn"));
        animations[1] = new Animation<>(FRAME_DURATION, atlas.findRegions("spr_idle"));
        animations[2] = new Animation<>(FRAME_DURATION, atlas.findRegions("spr_run"));
        animations[3] = new Animation<>(FRAME_DURATION, atlas.findRegions("spr_die"));
        animations[4] = new Animation<>(FRAME_DURATION, atlas.findRegions("spr_attack"));

        width = animations[0].getKeyFrame(0).getRegionWidth();
        height = animations[0].getKeyFrame(0).getRegionHeight();

        attack = new EarthAttack(animations[4], this, DEAL_DAMAGE_TIME.ONCE);
    }

    @Override
    public void draw() {
        statetime += Gdx.graphics.getDeltaTime();

        width = animations[animationIdx].getKeyFrame(statetime).getRegionWidth();
        height = animations[animationIdx].getKeyFrame(statetime).getRegionHeight();

        hitbox.x = x - width / 2;
        hitbox.y = y;

        hitbox.width = width;
        hitbox.height = height / 2;

        if(dead && animations[animationIdx].isAnimationFinished(statetime)){
            exist = false;
            return;
        }

        if(attacking) attack.update(statetime);

        MyGdxGame.batch.draw(animations[animationIdx].getKeyFrame(statetime, true), (flip ? width / 2 : -width / 2) + x, y, (flip ? -1 : 1) * width, height);

        if((animationIdx == 4 || animationIdx == 0) && animations[animationIdx].isAnimationFinished(statetime)){
            vulnerable = true;
            animationIdx = 2;
            if(attacking){
                attacking = false;
                attack.end();
            }
        }

        if(animationIdx > 0 && animationIdx < 3){
            move(GameManager.getInstance().getCurrentPlayer().getX(), GameManager.getInstance().getCurrentPlayer().getY());
        }
        if(!dead) update();
    }

    @Override
    public void move(float desX, float desY){
        if(abs(x - desX) < attackRange && abs(y - desY) < attackRange) {
            attack((int) desX, (int) desY);
            return;
        }

        flip = desX < x;

        float deltaSP = currentSpeed * Gdx.graphics.getDeltaTime();

        if(abs(x - desX) < 5){
            if(desY > y) y += deltaSP;
            else y -= deltaSP;
            return;
        }

        if(abs(y - desY) < 5){
            if(desX > x) x += deltaSP;
            else x -= deltaSP;
            return;
        }

        float tan = (y - desY) / (x - desX);

        angle = (float)Math.atan(tan);
        angle = angle * (float)(180 / Math.PI);

        if((angle > 0 && y > desY)
                || (angle < 0 && y < desY)) angle += 180;
        else if(angle < 0) angle += 360;

        xSpeed = (float) sqrt((currentSpeed * currentSpeed) / (1 + tan * tan));
        ySpeed = abs(xSpeed * tan);

        if(angle > 90 && angle < 270) xSpeed *= -1;
        if(angle > 180 && angle < 360) ySpeed *= -1;

        float xDist = xSpeed * Gdx.graphics.getDeltaTime();
        float yDist = ySpeed * Gdx.graphics.getDeltaTime();

        if(desX > x) x = Math.min(desX, x + xDist);
        else x = Math.max(desX, x + xDist);

        if(desY > y) y = Math.min(desY, y + yDist);
        else y = Math.max(desY, y + yDist);
    }

    @Override
    public void attack(int x, int y) {
        animationIdx = 4;
        statetime = 0;
        attacking = true;
        attack.start();
    }
}
