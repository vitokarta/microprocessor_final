// package code;

// import javafx.scene.image.Image;
// import javafx.scene.image.ImageView;
// import javafx.scene.layout.AnchorPane;

// import java.util.ArrayList;
// import java.util.HashSet;
// import java.util.List;
// import java.util.Set;

// public class Player {

//     private static final String IMAGE_PATH = "file:/C:/Users/vitok/OneDrive/桌面/Course Note/大三上/微算機/project_java/demo/resource/player.png";
//     private static final String MELEE_WEAPON_PATH = "file:/C:/Users/vitok/OneDrive/桌面/Course Note/大三上/微算機/project_java/demo/resource/bat.png";
//     private static final String RANGED_WEAPON_PATH = "file:/C:/Users/vitok/OneDrive/桌面/Course Note/大三上/微算機/project_java/demo/resource/gun.png";

//     private final ImageView playerImage; // 角色圖片
//     private final double moveStep; // 每次移動的距離
//     private final double mapWidth; // 地圖寬度
//     private final double mapHeight; // 地圖高度
//     private int health; // 玩家生命值
//     private final List<ImageView> weapons; // 玩家持有的武器
//     private final List<String> weaponTypes; // 每個武器的類型
    
//     private final Set<Integer> animatedWeapons = new HashSet<>();// 追蹤正在執行動畫的武器

//     private AnchorPane root;

//     private boolean isCollisionCooldown = false; // 用於冷卻時間的標誌

//     public Player(double mapWidth, double mapHeight, AnchorPane root) {
//         this.mapWidth = mapWidth;
//         this.mapHeight = mapHeight;
//         this.moveStep = 20.0;
//         this.health = 10; // 初始化生命值為 10
//         this.weapons = new ArrayList<>(); // 初始化武器列表
//         this.root = root; // 初始化主容器引用
//         this.weaponTypes = new ArrayList<>(); // 初始化武器類型列表
//         Image image = new Image(IMAGE_PATH);
//         playerImage = new ImageView(image);
//         playerImage.setFitWidth(50); // 設置圖片寬度
//         playerImage.setFitHeight(50); // 設置圖片高度
//         centerPlayer();
//     }

//     public void moveUp() {
//         double newY = playerImage.getY() - moveStep;
//         if (newY >= 0) {
//             playerImage.setY(newY);
//             updateWeaponPositions();
//         }
//     }

//     public void moveDown() {
//         double newY = playerImage.getY() + moveStep;
//         if (newY + playerImage.getFitHeight() <= mapHeight) {
//             playerImage.setY(newY);
//             updateWeaponPositions();
//         }
//     }

//     public void moveLeft() {
//         double newX = playerImage.getX() - moveStep;
//         if (newX >= 0) {
//             playerImage.setX(newX);
//             updateWeaponPositions();
//         }
//     }

//     public void moveRight() {
//         double newX = playerImage.getX() + moveStep;
//         if (newX + playerImage.getFitWidth() <= mapWidth) {
//             playerImage.setX(newX);
//             updateWeaponPositions();
//         }
//     }

//     public ImageView getImageView() {
//         return playerImage;
//     }

//     private void centerPlayer() {
//         playerImage.setX((mapWidth - playerImage.getFitWidth()) / 2);
//         playerImage.setY((mapHeight - playerImage.getFitHeight()) / 2);
//     }

//     public int getHealth() {
//         return health;
//     }

//     public void decreaseHealth() {
//         if (health > 0) {
//             health--;
//             System.out.println("Player hit! Remaining health: " + health);
//         }
//         if (health == 0) {
//             System.out.println("Player is dead!");
//         }
//     }

//     public void addMeleeWeapon() {
//         if (weapons.size() < 6) {
//             ImageView weapon = new ImageView(new Image(MELEE_WEAPON_PATH));
//             weapon.setFitWidth(70);
//             weapon.setFitHeight(70);
//             if (weapons.size() % 2 == 1) {
//                 weapon.setScaleX(-1); // 偶數次生成的武器水平反轉
//             }
//             weapons.add(weapon);
//             weaponTypes.add("MELEE"); 
//             positionWeapons();
//             root.getChildren().add(weapon); // 將武器添加到場景中
//             System.out.println("Melee weapon added! Total weapons: " + weapons.size());
//         } else {
//             System.out.println("Cannot add more weapons, maximum capacity reached!");
//         }
//     }

//     public void addRangedWeapon() {
//         if (weapons.size() < 6) {
//             ImageView weapon = new ImageView(new Image(RANGED_WEAPON_PATH));
//             weapon.setFitWidth(40);
//             weapon.setFitHeight(40);
//             if (weapons.size() % 2 == 1) {
//                 weapon.setScaleX(-1); // 偶數次生成的武器水平反轉
//             }
//             weapons.add(weapon);
//             weaponTypes.add("RANGED"); // 記錄武器類型為 "RANGED"
//             positionWeapons();
//             root.getChildren().add(weapon); // 將武器添加到場景中
//             System.out.println("Ranged weapon added! Total weapons: " + weapons.size());
//         } else {
//             System.out.println("Cannot add more weapons, maximum capacity reached!");
//         }
//     }
//     public void attackMeleeWeapon(List<Enemy> enemies) {
//         if (enemies.isEmpty()) return; // 沒有敵人時不攻擊
    
//         double playerCenterX = playerImage.getX() + playerImage.getFitWidth() / 2;
//         double playerCenterY = playerImage.getY() + playerImage.getFitHeight() / 2;
    
//         // 找到最近的敵人
//         Enemy closestEnemy = null;
//         double closestDistance = Double.MAX_VALUE;
    
//         for (Enemy enemy : enemies) {
//             double enemyCenterX = enemy.getImageView().getX() + enemy.getImageView().getFitWidth() / 2;
//             double enemyCenterY = enemy.getImageView().getY() + enemy.getImageView().getFitHeight() / 2;
    
//             double distance = Math.sqrt(
//                 Math.pow(playerCenterX - enemyCenterX, 2) + Math.pow(playerCenterY - enemyCenterY, 2)
//             );
    
//             if (distance < closestDistance) {
//                 closestDistance = distance;
//                 closestEnemy = enemy;
//             }
//         }
    
//         if (closestEnemy != null) {
//             // 計算最近敵人的方向
//             double enemyCenterX = closestEnemy.getImageView().getX() + closestEnemy.getImageView().getFitWidth() / 2;
//             double enemyCenterY = closestEnemy.getImageView().getY() + closestEnemy.getImageView().getFitHeight() / 2;
    
//             double baseAngle = Math.toDegrees(Math.atan2(enemyCenterY - playerCenterY, enemyCenterX - playerCenterX));
    
//             // 執行每個近戰武器的揮擊動作
//             for (int i = 0; i < weapons.size(); i++) {
//                 if (!weaponTypes.get(i).equals("MELEE")) continue; // 僅對近戰武器進行攻擊動作
    
//                 // 將 i 賦值到一個本地變數
//                 final int weaponIndex = i;
    
//                 ImageView meleeWeapon = weapons.get(weaponIndex);
//                 if (weaponIndex % 2 == 1) meleeWeapon.setScaleX(1);
    
//                 // 添加到動畫追蹤集合
//                 animatedWeapons.add(weaponIndex);
    
//                 double originalAngle = meleeWeapon.getRotate();
//                 double originalX = meleeWeapon.getX();
//                 double originalY = meleeWeapon.getY();
    
//                 // 計算武器攻擊的偏移量
//                 double angleOffset = (weaponIndex % 2 == 0 ? -10 : 10) + weaponIndex * 5; // 每把武器增加偏移角度
//                 double distanceOffset = 10 + weaponIndex * 5; // 每把武器增加偏移距離
    
//                 double attackAngle = baseAngle + angleOffset;
    
//                 // 設置揮擊的目標位置
//                 double radians = Math.toRadians(attackAngle);
//                 double targetX = playerCenterX + (50 + distanceOffset) * Math.cos(radians) - meleeWeapon.getFitWidth() / 2;
//                 double targetY = playerCenterY + (50 + distanceOffset) * Math.sin(radians) - meleeWeapon.getFitHeight() / 2;
    
//                 meleeWeapon.setRotate(attackAngle - 45); //45 初始位置，揮擊從 -45 度開始
//                 meleeWeapon.setX(targetX);
//                 meleeWeapon.setY(targetY);
    
//                 // 動態模擬揮擊
//                 new Thread(() -> {
//                     try {
//                         // 前進到目標位置
//                         for (int step = 0; step <= 10; step++) {
//                             final double t = step / 10.0;
//                             double radius = playerImage.getFitWidth() / 2 + 30; // 半徑距離，武器圍繞角色的距離
//                             double curAngle = attackAngle - 45 + t * 90; // 計算當前旋轉角度
                        
//                             // 計算當前武器相對角色中心的位置
//                             double curX = playerImage.getX() + playerImage.getFitWidth() / 2 + radius * Math.cos(Math.toRadians(curAngle)) - meleeWeapon.getFitWidth() / 2;
//                             double curY = playerImage.getY() + playerImage.getFitHeight() / 2 + radius * Math.sin(Math.toRadians(curAngle)) - meleeWeapon.getFitHeight() / 2;
                        
//                             javafx.application.Platform.runLater(() -> {
//                                 meleeWeapon.setX(curX); // 更新武器的X位置
//                                 meleeWeapon.setY(curY); // 更新武器的Y位置
//                                 meleeWeapon.setRotate(curAngle); // 更新武器的旋轉角度
//                             });
//                             Thread.sleep(50); // 每50ms更新一次
//                         }
    
//                         // // 回到角色位置動畫
//                         // for (int step = 0; step <= 10; step++) { // 增加步數，讓動畫更平滑
//                         //     final double t = step / 10.0; // 將動畫分成更多的步驟
//                         //     double curPlayerCenterX = playerImage.getX() + playerImage.getFitWidth() / 2;
//                         //     double curPlayerCenterY = playerImage.getY() + playerImage.getFitHeight() / 2;

//                         //     // 計算武器在回到角色位置過程中的位置
//                         //     double interpolatedX = targetX + t * (curPlayerCenterX - targetX);
//                         //     double interpolatedY = targetY + t * (curPlayerCenterY - targetY);

//                         //     javafx.application.Platform.runLater(() -> {
//                         //         meleeWeapon.setX(interpolatedX); // 更新武器的 X 坐標
//                         //         meleeWeapon.setY(interpolatedY); // 更新武器的 Y 坐標
//                         //     });

//                         //     Thread.sleep(30); // 每 30ms 更新一次
//                         // }

    
//                         javafx.application.Platform.runLater(() -> {
//                             if (weaponIndex % 2 == 1) meleeWeapon.setScaleX(-1);
//                             meleeWeapon.setRotate(originalAngle); // 恢復原始角度
//                             animatedWeapons.remove(Integer.valueOf(weaponIndex));
//                             positionWeapons(); // 恢復位置綁定
//                         });
//                     } catch (InterruptedException e) {
//                         e.printStackTrace();
//                     }
//                 }).start();
//             }
//         }
//     }
    
    
    
    

//     private void positionWeapons() {
//         double centerX = playerImage.getX() + playerImage.getFitWidth() / 2;
//     double centerY = playerImage.getY() + playerImage.getFitHeight() / 2;

//     double radius = playerImage.getFitWidth() / 2 + 20; // 武器與角色的半徑距離

//     for (int i = 0; i < weapons.size(); i++) {
//         // 跳過正在執行動畫的武器
//         if (animatedWeapons.contains(i)) continue;

//         ImageView weapon = weapons.get(i);
//         double angle;

//         switch (i) {
//             case 0: angle = 0; break;        // 右中
//             case 1: angle = 180; break;     // 左中
//             case 2: angle = 45; break;      // 右上
//             case 3: angle = 135; break;     // 左上
//             case 4: angle = -45; break;     // 右下
//             case 5: angle = -135; break;    // 左下
//             default: continue;
//         }

//         // 計算武器的位置
//         double radians = Math.toRadians(angle);
//         double weaponX = centerX + radius * Math.cos(radians) - weapon.getFitWidth() / 2;
//         double weaponY = centerY + radius * Math.sin(radians) - weapon.getFitHeight() / 2;

//         weapon.setX(weaponX);
//         weapon.setY(weaponY);
//     }
//     }
//     public boolean checkCollision(Enemy enemy) {
//         ImageView enemyImage = enemy.getImageView();
//         double playerX = playerImage.getX();
//         double playerY = playerImage.getY();
//         double playerWidth = playerImage.getFitWidth();
//         double playerHeight = playerImage.getFitHeight();

//         double enemyX = enemyImage.getX();
//         double enemyY = enemyImage.getY();
//         double enemyWidth = enemyImage.getFitWidth();
//         double enemyHeight = enemyImage.getFitHeight();

//         // 判斷兩個矩形是否相交（碰撞檢測）
//         boolean isColliding = playerX < enemyX + enemyWidth &&
//                               playerX + playerWidth > enemyX &&
//                               playerY < enemyY + enemyHeight &&
//                               playerY + playerHeight > enemyY;

//         if (isColliding) {
//             decreaseHealth();
//             return true; // 碰撞發生
//         }
//         return false; // 無碰撞
//     }
//     public void updateWeaponDirections(List<Enemy> enemies) {
//         if (enemies.isEmpty()) return; // 如果沒有敵人，不更新武器方向

//         double playerCenterX = playerImage.getX() + playerImage.getFitWidth() / 2;
//         double playerCenterY = playerImage.getY() + playerImage.getFitHeight() / 2;

//         for (int i = 0; i < weapons.size(); i++) {
//             ImageView weapon = weapons.get(i);

//             // 僅當武器類型為 "RANGED" 時執行瞄準
//             if (!weaponTypes.get(i).equals("RANGED")) {
//                 continue;
//             }

//             Enemy closestEnemy = null;
//             double closestDistance = Double.MAX_VALUE;

//             for (Enemy enemy : enemies) {
//                 double enemyCenterX = enemy.getImageView().getX() + enemy.getImageView().getFitWidth() / 2;
//                 double enemyCenterY = enemy.getImageView().getY() + enemy.getImageView().getFitHeight() / 2;

//                 double distance = Math.sqrt(
//                     Math.pow(playerCenterX - enemyCenterX, 2) + Math.pow(playerCenterY - enemyCenterY, 2)
//                 );

//                 if (distance < closestDistance) {
//                     closestDistance = distance;
//                     closestEnemy = enemy;
//                 }
//             }

//             if (closestEnemy != null) {
//                 // 計算槍枝面向最近敵人的角度
//                 double enemyCenterX = closestEnemy.getImageView().getX() + closestEnemy.getImageView().getFitWidth() / 2;
//                 double enemyCenterY = closestEnemy.getImageView().getY() + closestEnemy.getImageView().getFitHeight() / 2;
    
//                 double angle = Math.toDegrees(Math.atan2(enemyCenterY - playerCenterY, enemyCenterX - playerCenterX));
    
//                 // 確保槍枝保持“持正”，限制旋轉角度範圍為 -90 到 90 度
//                 if (angle > 90 || angle < -90) {
//                     angle = angle > 0 ? 180 - angle : -180 - angle;
//                     weapon.setScaleX(-1); // 翻轉垂直方向保持持正
//                 } else {
//                     weapon.setScaleX(1); // 恢復正常垂直方向
//                 }
    
//                 weapon.setRotate(angle); // 設置槍枝的旋轉角度
//             }
//         }
//     }
    
//     public void handleSpaceKeyAttack(List<Enemy> enemies) {
//         attackMeleeWeapon(enemies); // 觸發近戰攻擊
//     }

//     private void updateWeaponPositions() {
//         positionWeapons();
//     }

//     public List<ImageView> getWeapons() {
//         return weapons;
//     }
// }
