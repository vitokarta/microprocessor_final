package code;

import java.util.Set;
import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

public class WanderingEnemy extends Enemy {

    private static final double SPEED = 1.5;
    private final Random random = new Random();
    private double dx; // 當前 X 軸方向
    private double dy; // 當前 Y 軸方向
    private Timeline directionChangeTimeline; // 用於定時改變方向
    private Timeline bulletFireTimeline; // 用於定時發射子彈
    private final Set<Thread> bulletThreads = new HashSet<>(); // 存儲所有子彈的飛行線程
    private static final String BULLET_PATH = "file:/C:/Users/vitok/OneDrive/桌面/Course Note/大三上/微算機/project_java/demo/resource/bullet2.png";

    // 靜態變數，用於標記遊戲是否已結束
    private static boolean isGameOver = false;
    private volatile boolean areBulletsPaused = false; // 子彈是否暫停

    public WanderingEnemy(double mapWidth, double mapHeight, AnchorPane root, Player player) {
        super("file:/C:/Users/vitok/OneDrive/桌面/Course Note/大三上/微算機/project_java/demo/resource/monster2.png",
              mapWidth, mapHeight, root, player);
        setRandomDirection(); // 初始化隨機方向
        startDirectionChangeTimer(); // 開始定時改變方向
        startBulletFireTimer(); // 開始定時發射子彈
    }

    // 每 1 秒隨機改變方向
    private void startDirectionChangeTimer() {
        directionChangeTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> setRandomDirection()));
        directionChangeTimeline.setCycleCount(Timeline.INDEFINITE);
        directionChangeTimeline.play();
    }

    // 每 5 秒發射一次子彈
    private void startBulletFireTimer() {
        scheduleNextBulletFire();
    }
    // 設置下一次子彈發射的時間
    private void scheduleNextBulletFire() {
        double randomInterval = 5 + Math.random() * 5; // 產生 5 到 10 秒之間的隨機值
        bulletFireTimeline = new Timeline(new KeyFrame(Duration.seconds(randomInterval), e -> {
            fireBullet();
            scheduleNextBulletFire(); // 再次調用，為下一次發射設置新的隨機時間
        }));
        bulletFireTimeline.setCycleCount(1); // 只執行一次
        bulletFireTimeline.play();
    }

    // 停止方向變更計時器（例如在敵人被移除時）
    public void stopDirectionChangeTimer() {
        if (directionChangeTimeline != null) {
            directionChangeTimeline.stop();
        }
        if (bulletFireTimeline != null) {
            bulletFireTimeline.stop();
        }
    }

    // 設置隨機方向
    private void setRandomDirection() {
        dx = (random.nextDouble() * 2 - 1) * SPEED; // 隨機 -1 至 1 的 X 偏移
        dy = (random.nextDouble() * 2 - 1) * SPEED; // 隨機 -1 至 1 的 Y 偏移
    }

    @Override
    public void update(double playerX, double playerY) {
        double newX = enemyImage.getX() + dx;
        double newY = enemyImage.getY() + dy;
        if (dx > 0) {
            enemyImage.setScaleX(-1);
        } else {
            enemyImage.setScaleX(1);
        }
        // 邊界檢查
        if (newX >= 0 && newX <= mapWidth - enemyImage.getFitWidth()) {
            enemyImage.setX(newX);
        } else {
            dx = -dx; // 碰到邊界時反向
        }

        if (newY >= 0 && newY <= mapHeight - enemyImage.getFitHeight()) {
            enemyImage.setY(newY);
        } else {
            dy = -dy; // 碰到邊界時反向
        }
    }

    private void fireBullet() {
        if (isGameOver) return; // 如果遊戲已結束，則不發射子彈

        ImageView bullet = new ImageView(new Image(BULLET_PATH));
        bullet.setFitWidth(30);
        bullet.setFitHeight(30);

        // 設置子彈初始位置為敵人中心
        double enemyCenterX = enemyImage.getX() + enemyImage.getFitWidth() / 2;
        double enemyCenterY = enemyImage.getY() + enemyImage.getFitHeight() / 2;
        bullet.setX(enemyCenterX - bullet.getFitWidth() / 2);
        bullet.setY(enemyCenterY - bullet.getFitHeight() / 2);

        root.getChildren().add(bullet);

        ImageView playerImage = player.getImageView();
        double playerCenterX = playerImage.getX() + playerImage.getFitWidth() / 2;
        double playerCenterY = playerImage.getY() + playerImage.getFitHeight() / 2;

        double angle = Math.atan2(playerCenterY - enemyCenterY, playerCenterX - enemyCenterX);

        AtomicBoolean hitOrOutOfBounds = new AtomicBoolean(false);

        // 動態模擬子彈飛行
        Thread bulletThread = new Thread(() -> {
            try {
                while (!hitOrOutOfBounds.get() && !isGameOver) {
                    synchronized (this) {
                        while (areBulletsPaused) {
                            wait(); // 暫停時等待
                        }
                    }
                    javafx.application.Platform.runLater(() -> {
                        bullet.setX(bullet.getX() + 5 * Math.cos(angle));
                        bullet.setY(bullet.getY() + 5 * Math.sin(angle));

                        // 檢查子彈是否與玩家碰撞
                        player.checkCollision(bullet);

                        // 檢查子彈是否超出邊界
                        if (bullet.getX() < 0 || bullet.getX() > mapWidth || bullet.getY() < 0 || bullet.getY() > mapHeight) {
                            root.getChildren().remove(bullet); // 移除子彈
                            hitOrOutOfBounds.set(true); // 停止飛行
                        }
                    });
                    Thread.sleep(50); // 每 50ms 更新一次
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        bulletThread.setDaemon(true);
        bulletThread.start();
        bulletThreads.add(bulletThread);
    }

    public void pauseBullets() {
        areBulletsPaused = true;
    }

    public void resumeBullets() {
        synchronized (this) {
            areBulletsPaused = false;
            notifyAll();
        }
    }

    @Override
    public void remove() {
        super.remove();
        stopDirectionChangeTimer();
        stopBulletFireTimer();
    }

    public void resumeBulletFireTimer() {
        if (bulletFireTimeline != null) {
            bulletFireTimeline.play();
        }
    }

    public void stopBulletFireTimer() {
        if (bulletFireTimeline != null) {
            bulletFireTimeline.stop();
        }
    }

    public static void setGameOver(boolean gameOver) {
        isGameOver = gameOver;
    }
}
