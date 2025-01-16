package code;

import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class ChasingEnemy extends Enemy {

    private static final double SPEED = 2.0;

    public ChasingEnemy(double mapWidth, double mapHeight, AnchorPane root, Player player) {
        super("file:/C:/Users/vitok/OneDrive/桌面/Course Note/大三上/微算機/project_java/demo/resource/monster_melee.png", mapWidth, mapHeight, root, player);
    }

    @Override
    public void update(double playerX, double playerY) {
        double dx = playerX - enemyImage.getX();
        double dy = playerY - enemyImage.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);

        //如果與玩家角度<90 or >-90 則轉向
        if (dx > 0) {
            enemyImage.setScaleX(1);
        } else {
            enemyImage.setScaleX(-1);
        }
        // 正規化向量並按速度更新位置
        if (distance > 0) {
            enemyImage.setX(enemyImage.getX() + SPEED * dx / distance);
            enemyImage.setY(enemyImage.getY() + SPEED * dy / distance);
        }
    }
}
