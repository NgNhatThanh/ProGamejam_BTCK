package btck.com.crowd_control;

import btck.com.common.GameManager;
import btck.com.common.sound.ConstantSound;
import btck.com.controller.attack.Bullet;
import btck.com.model.entity.Enemy;
import btck.com.model.entity.Entity;
import btck.com.view.screens.IngameScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

public class SlowMo {

    public static boolean activeAll = false;

    public static float slowAmount = 15f;

    static Sound slowAllSfx = Gdx.audio.newSound(Gdx.files.internal("sound/sound ingame/slowtime.mp3"));

    public static void activateAll(){
        if(activeAll) return;

        slowAllSfx.play(ConstantSound.getInstance().getSoundVolume());
        ConstantSound.getInstance().setBgmVolume(ConstantSound.getInstance().getSoundVolume() / 3);

        activeAll = true;

        for(Bullet bullet : IngameScreen.getBullets()){
            bullet.setSpeed(bullet.getSpeed() / slowAmount);
        }

        for (Enemy tmp : GameManager.getInstance().getEnemies()) {
            activateEntity(tmp);
        }
    }

    public static void deactivateAll(){
        if(!activeAll) return;
        activeAll = false;

        slowAllSfx.stop();
        ConstantSound.getInstance().setBgmVolume(ConstantSound.getInstance().getSoundVolume() * 3);

        for(Bullet bullet : IngameScreen.getBullets()){
            bullet.setSpeed(bullet.getSpeed() * slowAmount);
        }

        for (Enemy tmp : GameManager.getInstance().getEnemies()) {
            deactivateEntity(tmp);
        }
    }

    public static void activateEntity(Entity entity){
        entity.setFRAME_DURATION(entity.getFRAME_DURATION() * slowAmount);
        entity.setNormalSpeed(entity.getNormalSpeed() / slowAmount);
        entity.setCurrentSpeed(entity.getCurrentSpeed() / slowAmount);
        entity.setAttackSpeed(entity.getAttackSpeed() / slowAmount);
    }

    public static void deactivateEntity(Entity entity){
        entity.setFRAME_DURATION(entity.getFRAME_DURATION() / slowAmount);
        entity.setNormalSpeed(entity.getNormalSpeed() * slowAmount);
        entity.setCurrentSpeed(entity.getCurrentSpeed() * slowAmount);
        entity.setAttackSpeed(entity.getAttackSpeed() * slowAmount);
    }

}
