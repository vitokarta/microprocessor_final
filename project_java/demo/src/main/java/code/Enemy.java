package code;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public abstract class Enemy {

    protected ImageView enemyImage;
    protected double mapWidth;
    protected double mapHeight;
    protected AnchorPane root; // 添加 root 引用
    protected Player player; // 添加 player 引用
    private boolean isRemoved = false; // 標記是否已被移除
    private int health; // 敵人的生命值

    public Enemy(String imagePath, double mapWidth, double mapHeight, AnchorPane root, Player player) {
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.root = root; // 初始化 root
        this.player = player; // 初始化 playerImage
        this.enemyImage = new ImageView(new Image(imagePath));
        this.enemyImage.setFitWidth(40);
        this.enemyImage.setFitHeight(40);
        this.health = 3; // 預設每個敵人初始生命值為 3
        initializePosition();
    }

    // 初始化在地圖邊緣的隨機位置
    private void initializePosition() {
        double position = Math.random();
        if (position < 0.25) { // 上邊
            enemyImage.setX(Math.random() * mapWidth);
            enemyImage.setY(0);
        } else if (position < 0.5) { // 下邊
            enemyImage.setX(Math.random() * mapWidth);
            enemyImage.setY(mapHeight - enemyImage.getFitHeight());
        } else if (position < 0.75) { // 左邊
            enemyImage.setX(0);
            enemyImage.setY(Math.random() * mapHeight);
        } else { // 右邊
            enemyImage.setX(mapWidth - enemyImage.getFitWidth());
            enemyImage.setY(Math.random() * mapHeight);
        }
    }

    // 更新敵人的移動邏輯
    public abstract void update(double playerX, double playerY);

    public ImageView getImageView() {
        return enemyImage;
    }

    // 標記敵人為已移除並清理資源
    public void remove() {
        if (!isRemoved) {
            isRemoved = true;
        }
    }

    public boolean isRemoved() {
        return isRemoved;
    }

    // 減少生命值的方法
    public void decreaseHealth(int amount) {
        if (isRemoved) return; // 如果敵人已移除，忽略扣血

        health -= amount;
        if (health <= 0) {
            remove(); // 當生命值降為 0，標記敵人為已移除
        }
    }

    // 獲取目前生命值
    public int getHealth() {
        return health;
    }
}
