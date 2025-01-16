// package code;

// import javafx.animation.KeyFrame;
// import javafx.animation.Timeline;
// import javafx.fxml.FXML;
// import javafx.scene.input.KeyEvent;
// import javafx.scene.layout.AnchorPane;
// import javafx.util.Duration;

// import java.util.ArrayList;
// import java.util.HashSet;
// import java.util.List;
// import java.util.Set;

// public class Scene1Controller {

//     @FXML
//     private AnchorPane root; // 主容器

//     private Timeline moveTimeline; // 控制持續移動的時間軸
//     private Timeline enemySpawnTimeline; // 控制敵人生成的時間軸
//     private Timeline enemyUpdateTimeline; // 控制敵人行為更新的時間軸

//     private Set<String> activeKeys = new HashSet<>(); // 當前按下的鍵集合
//     private List<Enemy> enemies = new ArrayList<>(); // 保存所有敵人

//     private Player player; // Player 角色實例

//     @FXML
//     public void initialize() {
//         // 設置地圖尺寸
//         double mapWidth = 1050;
//         double mapHeight = 600;

//         // 初始化 Player
//         player = new Player(mapWidth, mapHeight);
//         root.getChildren().add(player.getImageView());

//         // 初始化 Timeline
//         moveTimeline = new Timeline(new KeyFrame(Duration.millis(50), e -> movePlayer()));
//         moveTimeline.setCycleCount(Timeline.INDEFINITE);
//         moveTimeline.play();

//         // 每秒生成敵人
//         enemySpawnTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> spawnEnemy(mapWidth, mapHeight)));
//         enemySpawnTimeline.setCycleCount(Timeline.INDEFINITE);
//         enemySpawnTimeline.play();

//         // 更新敵人行為
//         enemyUpdateTimeline = new Timeline(new KeyFrame(Duration.millis(50), e -> updateEnemies()));
//         enemyUpdateTimeline.setCycleCount(Timeline.INDEFINITE);
//         enemyUpdateTimeline.play();

//         // 設置鍵盤事件監聽
//         root.setOnKeyPressed(this::handleKeyPress);
//         root.setOnKeyReleased(this::handleKeyRelease);

//         // 讓容器獲取鍵盤焦點
//         root.setFocusTraversable(true);
//     }

//     private void handleKeyPress(KeyEvent event) {
//         activeKeys.add(event.getCode().toString());
//     }

//     private void handleKeyRelease(KeyEvent event) {
//         activeKeys.remove(event.getCode().toString());
//     }

//     private void movePlayer() {
//         if (activeKeys.contains("W")) {
//             player.moveUp();
//         }
//         if (activeKeys.contains("S")) {
//             player.moveDown();
//         }
//         if (activeKeys.contains("A")) {
//             player.moveLeft();
//         }
//         if (activeKeys.contains("D")) {
//             player.moveRight();
//         }
//     }

//     private void spawnEnemy(double mapWidth, double mapHeight) {
//         Enemy enemy;
//         if (Math.random() < 0.5) {
//             // 第一種敵人：追蹤玩家
//             enemy = new ChasingEnemy(mapWidth, mapHeight);
//         } else {
//             // 第二種敵人：隨機遊走
//             enemy = new WanderingEnemy(mapWidth, mapHeight);
//         }
//         enemies.add(enemy);
//         root.getChildren().add(enemy.getImageView());
//     }

//     private void updateEnemies() {
//         double playerX = player.getImageView().getX() + player.getImageView().getFitWidth() / 2 - 20;//
//         double playerY = player.getImageView().getY() + player.getImageView().getFitHeight() / 2 - 20; //
//         for (Enemy enemy : enemies) {
//             enemy.update(playerX, playerY);
//         }
//     }
// }
