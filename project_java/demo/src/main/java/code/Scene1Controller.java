package code;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;


public class Scene1Controller {

    @FXML
    private AnchorPane root; // 主容器

    @FXML
    private ProgressBar healthBar; // 對應 FXML 中的 healthBar

    @FXML
    private ProgressBar expBar; // 對應 FXML 中的 expBar

    @FXML
    private AnchorPane pauseOverlay; // 對應 FXML 中的 levelBar
    @FXML
    private ImageView reward;
    @FXML
    private ImageView spinTable;

    private Timeline moveTimeline; // 控制持續移動的時間軸
    private Timeline enemySpawnTimeline; // 控制敵人生成的時間軸
    private Timeline enemyUpdateTimeline; // 控制敵人行為更新的時間軸

    private Set<String> activeKeys = new HashSet<>(); // 當前按下的鍵集合
    private List<Enemy> enemies = new ArrayList<>(); // 保存所有敵人

    private Player player; // Player 角色實例
    private static final int MAX_HEALTH = 10; // 最大生命值
    private boolean isPaused = false; // 標記遊戲是否暫停
    private AtomicBoolean attackCooldown;
    private AtomicReference<Double> spawnInterval;

    
    private static final String MELEE_WEAPON_PATH = "file:/C:/Users/vitok/OneDrive/桌面/Course Note/大三上/微算機/project_java/demo/resource/bat.png";
    private static final String RANGED_WEAPON_PATH = "file:/C:/Users/vitok/OneDrive/桌面/Course Note/大三上/微算機/project_java/demo/resource/gun.png";
    private static final String HEART = "file:/C:/Users/vitok/OneDrive/桌面/Course Note/大三上/微算機/project_java/demo/resource/heart.png";
    private static final String ATTACK = "file:/C:/Users/vitok/OneDrive/桌面/Course Note/大三上/微算機/project_java/demo/resource/attack.png";

    @FXML
    public void initialize() {
        // 設置地圖尺寸
        double mapWidth = 1050;
        double mapHeight = 600;
    
        // 初始化 Player
        player = new Player(mapWidth, mapHeight, root);
        root.getChildren().add(player.getImageView());
        player.addMeleeWeapon(); // 初始化玩家的近戰武器
    
        // 初始化生成間隔
        final double initialSpawnInterval = 1.8; // 最初生成間隔 3 秒
        final double minSpawnInterval = 1.0; // 最短生成間隔 1 秒
        final double intervalDecrement = 0.2; // 每次減少的間隔時間
        final double intervalUpdateTime = 5.0; // 每 10 秒更新一次間隔
        spawnInterval = new AtomicReference<>(initialSpawnInterval);

        // 初始化 Timeline
        moveTimeline = new Timeline(new KeyFrame(Duration.millis(50), e -> {
            if (!player.isPausedForLevelUp) {
                movePlayer();
            }
        }));
        moveTimeline.setCycleCount(Timeline.INDEFINITE);
        moveTimeline.play();

        // 更新敵人生成間隔
        Timeline spawnIntervalUpdateTimeline = new Timeline(new KeyFrame(Duration.seconds(intervalUpdateTime), e -> {
            double currentInterval = spawnInterval.get();
            if (currentInterval > minSpawnInterval) {
                spawnInterval.set(Math.max(currentInterval - intervalDecrement, minSpawnInterval));
            }
        }));
        spawnIntervalUpdateTimeline.setCycleCount(Timeline.INDEFINITE);
        spawnIntervalUpdateTimeline.play();

        // 每秒生成敵人
        enemySpawnTimeline = new Timeline(new KeyFrame(Duration.seconds(spawnInterval.get()), e -> {
            if (!player.isPausedForLevelUp) {
                spawnEnemy(mapWidth, mapHeight);
            }
        }));
        enemySpawnTimeline.setCycleCount(Timeline.INDEFINITE);
        enemySpawnTimeline.play();
    
        // 更新敵人行為
        enemyUpdateTimeline = new Timeline(new KeyFrame(Duration.millis(50), e -> {
            if (!player.isPausedForLevelUp) {
                updateEnemies();
            }
        }));
        enemyUpdateTimeline.setCycleCount(Timeline.INDEFINITE);
        enemyUpdateTimeline.play();
    
        // 自動遠程攻擊
        Timeline rangedAttackTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            if (!player.isPausedForLevelUp) {
                player.fireRangedWeapon(enemies);
            }
        }));
        rangedAttackTimeline.setCycleCount(Timeline.INDEFINITE);
        rangedAttackTimeline.play();
    
        // 自動近距離攻擊
        Timeline meleeAttackTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            if (!player.isPausedForLevelUp && !enemies.isEmpty()) {
                player.attackMeleeWeapon(enemies); // 若周圍有敵人，觸發近戰攻擊
            }
        }));
        meleeAttackTimeline.setCycleCount(Timeline.INDEFINITE);
        meleeAttackTimeline.play();
    
        // 設置鍵盤事件監聽
        root.setOnKeyPressed(this::handleKeyPress);
        root.setOnKeyReleased(this::handleKeyRelease);
    
        // 讓容器獲取鍵盤焦點
        root.setFocusTraversable(true);
    }
    

    private void handleKeyPress(KeyEvent event) {
        if (isPaused) {
            // 處理升級選擇或繼續遊戲的操作
            switch (event.getCode()) {
                case DIGIT1: // 增加近戰武器
                    player.addMeleeWeapon();
                    showRewardAndResume(MELEE_WEAPON_PATH);
                    break;
                case DIGIT2: // 增加遠程武器
                    player.addRangedWeapon();
                    showRewardAndResume(RANGED_WEAPON_PATH);
                    break;
                case DIGIT3: // 回復生命
                    player.heal();
                    showRewardAndResume(HEART);
                    break;
                case DIGIT4: // 增加攻擊力
                    player.addAttackPower();
                    showRewardAndResume(ATTACK);
                    break;
                default:
                    break;
            }
            return;
        }
    
        activeKeys.add(event.getCode().toString());
    
        if (event.getCode().toString().equals("SPACE")) {
            if (!attackCooldown.get()) { // 檢查冷卻是否結束
                player.handleSpaceKeyAttack(enemies); // 觸發攻擊
                attackCooldown.set(true); // 設置冷卻狀態
    
                // 設置冷卻時間為 1 秒
                new Timeline(new KeyFrame(Duration.seconds(1), e -> attackCooldown.set(false))).play();
            }
        }
    }

    private void handleKeyRelease(KeyEvent event) {
        activeKeys.remove(event.getCode().toString());
    }

    private void movePlayer() {
        if (activeKeys.contains("W")) {
            player.moveUp();
        }
        if (activeKeys.contains("S")) {
            player.moveDown();
        }
        if (activeKeys.contains("A")) {
            player.moveLeft();
        }
        if (activeKeys.contains("D")) {
            player.moveRight();
        }
    }

    private void spawnEnemy(double mapWidth, double mapHeight) {
        Enemy enemy;
        if (Math.random() < 0.5) {
            // 第一種敵人：追蹤玩家
            enemy = new ChasingEnemy(mapWidth, mapHeight, root, player);
        } else {
            // 第二種敵人：隨機遊走
            enemy = new WanderingEnemy(mapWidth, mapHeight, root, player);
        }
        enemies.add(enemy);
        root.getChildren().add(enemy.getImageView());
    }

    private void showRewardAndResume(String imagePath) {
        // 更新 `reward` 圖片
        reward.setImage(new Image(imagePath));
        spinTable.setVisible(false); // 隱藏轉盤
        reward.setVisible(true); // 顯示獎勵圖片

        // 2 秒後恢復狀態
        new Timeline(new KeyFrame(Duration.seconds(2), e -> {
            reward.setVisible(false); // 隱藏獎勵圖片
            spinTable.setVisible(true); // 恢復轉盤可見性
            resumeGame(); // 恢復遊戲
        })).play();
    }


    private void updateEnemies() {
        if (isPaused) {
            return; // 遊戲暫停時不更新敵人行為
        }
    
        double playerX = player.getImageView().getX() + player.getImageView().getFitWidth() / 2 - 20;
        double playerY = player.getImageView().getY() + player.getImageView().getFitHeight() / 2 - 20;
    
        List<Enemy> toRemove = new ArrayList<>(); // 用於存儲需要移除的敵人
    
        // 更新每個敵人
        for (Enemy enemy : enemies) {
            enemy.update(playerX, playerY);
    
            player.checkCollision(enemy);
            if(enemy.isRemoved()) {
                toRemove.add(enemy);
            }
        }
        // 移除所有被擊殺的敵人
        for (Enemy enemy : toRemove) {
            root.getChildren().remove(enemy.getImageView());
            enemies.remove(enemy);
            player.addExperience(1); // 擊殺敵人增加經驗
        }
    
        // 更新每個武器的方向以面向最近的敵人
        player.updateWeaponDirections(enemies);
        updateHealthBar();
        updateExpBar();
        if(player.isPausedForLevelUp == true && !isPaused) {
            pauseForLevelUp(player.getLevel());
        }
    
        // 檢查玩家是否死亡
        if (player.getHealth() <= 0) {
            endGame();
        }
    }
    private void updateHealthBar() {
        double progress = (double) player.getHealth() / MAX_HEALTH;
        healthBar.setProgress(progress);
    }

    private void updateExpBar() {
        double progress = player.getExperienceProgress();
        expBar.setProgress(progress);
    }

    // 暫停遊戲並顯示升級消息
    private void pauseForLevelUp(int newLevel) {
        isPaused = true;
        pauseOverlay.setVisible(true);
        pauseOverlay.toFront();
        moveTimeline.stop();
        enemySpawnTimeline.stop();
        enemyUpdateTimeline.stop();
        //WanderingEnemy.setGameOver(true); // 停止所有敵人的子彈飛行
        
        for (Enemy enemy : enemies) {
            if (enemy instanceof WanderingEnemy) {
                ((WanderingEnemy) enemy).stopBulletFireTimer(); // 停止 WanderingEnemy 的子彈計時器
                ((WanderingEnemy) enemy).pauseBullets();
            }
        }

        // javafx.application.Platform.runLater(() -> {
        //     javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        //     alert.setTitle("Level Up!");
        //     alert.setHeaderText(null);
        //     alert.setContentText("恭喜升到等級 " + newLevel + "！按 R 繼續遊戲。");
        //     alert.showAndWait();
        // });
    }

    // 恢復遊戲
    private void resumeGame() {
        isPaused = false;
        player.isPausedForLevelUp = false;
        pauseOverlay.setVisible(false);
        moveTimeline.play();
        enemySpawnTimeline.play();
        enemyUpdateTimeline.play();
        //WanderingEnemy.setGameOver(false); // 恢復所有敵人的子彈飛行
        for (Enemy enemy : enemies) {
            if (enemy instanceof WanderingEnemy) {
                ((WanderingEnemy) enemy).resumeBulletFireTimer(); // 停止 WanderingEnemy 的子彈計時器
                ((WanderingEnemy) enemy).resumeBullets();
            }
        }
    }


    

    private void endGame() {
        System.out.println("Game Over!");
    
        // 停止所有時間軸
        moveTimeline.stop();
        enemySpawnTimeline.stop();
        enemyUpdateTimeline.stop();
        WanderingEnemy.setGameOver(true); // 停止所有敵人的子彈飛行
    
        // 停止所有敵人的行為
        for (Enemy enemy : enemies) {
            if (enemy instanceof WanderingEnemy) {
                ((WanderingEnemy) enemy).stopBulletFireTimer(); // 停止 WanderingEnemy 的子彈計時器
            }
            enemy.remove(); // 確保敵人停止所有行為並被標記為已移除
        }
    
        // 清除敵人列表，確保不再更新或處理
        enemies.clear();
    
        // 顯示 "Game Over" 消息（可選）
        javafx.application.Platform.runLater(() -> {
            // 顯示遊戲結束訊息
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
            alert.setTitle("Game Over");
            alert.setHeaderText(null);
            alert.setContentText("遊戲結束！");
            alert.showAndWait();
        });
    }
    
}
