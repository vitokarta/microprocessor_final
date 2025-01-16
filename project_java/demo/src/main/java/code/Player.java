package code;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class Player {

    private static final String IMAGE_PATH = "file:/C:/Users/vitok/OneDrive/桌面/Course Note/大三上/微算機/project_java/demo/resource/player.png";
    private static final String MELEE_WEAPON_PATH = "file:/C:/Users/vitok/OneDrive/桌面/Course Note/大三上/微算機/project_java/demo/resource/bat.png";
    private static final String RANGED_WEAPON_PATH = "file:/C:/Users/vitok/OneDrive/桌面/Course Note/大三上/微算機/project_java/demo/resource/gun.png";
    private static final String BULLET_PATH = "file:/C:/Users/vitok/OneDrive/桌面/Course Note/大三上/微算機/project_java/demo/resource/bullet.png";


    private final ImageView playerImage; // 角色圖片
    private final double moveStep; // 每次移動的距離
    private final double mapWidth; // 地圖寬度
    private final double mapHeight; // 地圖高度
    private int attackPower; // 攻擊力
    private int health; // 玩家生命值
    private final List<ImageView> weapons; // 玩家持有的武器
    private final List<String> weaponTypes; // 每個武器的類型
    private int experience = 0; // 玩家當前經驗值
    private int level = 1; // 玩家當前等級
        
    private final Set<Integer> animatedWeapons = new HashSet<>();// 追蹤正在執行動畫的武器

    private AnchorPane root;

    private boolean isCollisionCooldown = false; // 用於冷卻時間的標誌
    public boolean isPausedForLevelUp = false; // 用於標記升級暫停狀態

    public Player(double mapWidth, double mapHeight, AnchorPane root) {
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.moveStep = 10.0;
        this.health = 10; // 初始化生命值為 10
        this.attackPower = 1; // 初始化攻擊力為 1
        this.weapons = new ArrayList<>(); // 初始化武器列表
        this.root = root; // 初始化主容器引用
        this.weaponTypes = new ArrayList<>(); // 初始化武器類型列表
        Image image = new Image(IMAGE_PATH);
        playerImage = new ImageView(image);
        playerImage.setFitWidth(50); // 設置圖片寬度
        playerImage.setFitHeight(50); // 設置圖片高度
        centerPlayer();
    }

    public void moveUp() {
        double newY = playerImage.getY() - moveStep;
        if (newY >= 0) {
            playerImage.setY(newY);
            updateWeaponPositions();
        }
    }

    public void moveDown() {
        double newY = playerImage.getY() + moveStep;
        if (newY + playerImage.getFitHeight() <= mapHeight) {
            playerImage.setY(newY);
            updateWeaponPositions();
        }
    }

    public void moveLeft() {
        double newX = playerImage.getX() - moveStep;
        if (newX >= 0) {
            playerImage.setX(newX);
            playerImage.setScaleX(-1);
            updateWeaponPositions();
        }
    }

    public void moveRight() {
        double newX = playerImage.getX() + moveStep;
        if (newX + playerImage.getFitWidth() <= mapWidth) {
            playerImage.setX(newX);
            playerImage.setScaleX(1);
            updateWeaponPositions();
        }
    }

    public ImageView getImageView() {
        return playerImage;
    }

    private void centerPlayer() {
        playerImage.setX((mapWidth - playerImage.getFitWidth()) / 2);
        playerImage.setY((mapHeight - playerImage.getFitHeight()) / 2);
    }

    public int getHealth() {
        return health;
    }

    public void decreaseHealth() {
        if (health > 0) {
            health--;
            System.out.println("Player hit! Remaining health: " + health);
        }
        if (health == 0) {
            System.out.println("Player is dead!");
        }
    }

    public void addMeleeWeapon() {
        if (weapons.size() < 6) {
            ImageView weapon = new ImageView(new Image(MELEE_WEAPON_PATH));
            weapon.setFitWidth(70);
            weapon.setFitHeight(70);
            if (weapons.size() % 2 == 1) {
                weapon.setScaleX(-1); // 偶數次生成的武器水平反轉
            }
            weapons.add(weapon);
            weaponTypes.add("MELEE"); 
            positionWeapons();
            root.getChildren().add(weapon); // 將武器添加到場景中
            System.out.println("Melee weapon added! Total weapons: " + weapons.size());
        } else {
            System.out.println("Cannot add more weapons, maximum capacity reached!");
        }
    }

    public void addRangedWeapon() {
        if (weapons.size() < 6) {
            ImageView weapon = new ImageView(new Image(RANGED_WEAPON_PATH));
            weapon.setFitWidth(40);
            weapon.setFitHeight(40);
            if (weapons.size() % 2 == 1) {
                weapon.setScaleX(-1); // 偶數次生成的武器水平反轉
            }
            weapons.add(weapon);
            weaponTypes.add("RANGED"); // 記錄武器類型為 "RANGED"
            positionWeapons();
            root.getChildren().add(weapon); // 將武器添加到場景中
            System.out.println("Ranged weapon added! Total weapons: " + weapons.size());
        } else {
            System.out.println("Cannot add more weapons, maximum capacity reached!");
        }
    }
    
    public void attackMeleeWeapon(List<Enemy> enemies) {
        if (enemies.isEmpty()) return; // 沒有敵人時不攻擊
    
        double playerCenterX = playerImage.getX() + playerImage.getFitWidth() / 2;
        double playerCenterY = playerImage.getY() + playerImage.getFitHeight() / 2;
    
        boolean hasNearbyEnemy = false;
    
        // 判斷是否有敵人在距離 60 內
        for (Enemy enemy : enemies) {
            double enemyCenterX = enemy.getImageView().getX() + enemy.getImageView().getFitWidth() / 2;
            double enemyCenterY = enemy.getImageView().getY() + enemy.getImageView().getFitHeight() / 2;
    
            double distance = Math.sqrt(
                Math.pow(playerCenterX - enemyCenterX, 2) + Math.pow(playerCenterY - enemyCenterY, 2)
            );
    
            if (distance <= 120) {
                hasNearbyEnemy = true;
                break; // 有敵人即可停止判斷
            }
        }
    
        // 若沒有附近的敵人，直接返回
        if (!hasNearbyEnemy) return;
    
        // 找到最近的敵人，執行近戰攻擊
        Enemy closestEnemy = null;
        double closestDistance = Double.MAX_VALUE;
    
        for (Enemy enemy : enemies) {
            double enemyCenterX = enemy.getImageView().getX() + enemy.getImageView().getFitWidth() / 2;
            double enemyCenterY = enemy.getImageView().getY() + enemy.getImageView().getFitHeight() / 2;
    
            double distance = Math.sqrt(
                Math.pow(playerCenterX - enemyCenterX, 2) + Math.pow(playerCenterY - enemyCenterY, 2)
            );
    
            if (distance < closestDistance) {
                closestDistance = distance;
                closestEnemy = enemy;
            }
        }
    
        if (closestEnemy != null) {
            // 計算最近敵人的方向
            double enemyCenterX = closestEnemy.getImageView().getX() + closestEnemy.getImageView().getFitWidth() / 2;
            double enemyCenterY = closestEnemy.getImageView().getY() + closestEnemy.getImageView().getFitHeight() / 2;
    
            double baseAngle = Math.toDegrees(Math.atan2(enemyCenterY - playerCenterY, enemyCenterX - playerCenterX));
    
            // 執行每個近戰武器的揮擊動作
            for (int i = 0; i < weapons.size(); i++) {
                if (!weaponTypes.get(i).equals("MELEE")) continue; // 僅對近戰武器進行攻擊動作
    
                // 將 i 賦值到一個本地變數
                final int weaponIndex = i;
    
                ImageView meleeWeapon = weapons.get(weaponIndex);
                if (weaponIndex % 2 == 1) meleeWeapon.setScaleX(1);
    
                // 添加到動畫追蹤集合
                animatedWeapons.add(weaponIndex);
                Set<Enemy> weaponDamagedEnemies = new HashSet<>();
    
                double originalAngle = meleeWeapon.getRotate();
    
                // 計算武器攻擊的偏移量
                double angleOffset = (weaponIndex % 2 == 0 ? -10 : 10) + weaponIndex * 5; // 每把武器增加偏移角度
                double distanceOffset = 10 + weaponIndex * 5; // 每把武器增加偏移距離
    
                double attackAngle = baseAngle + angleOffset;
    
                // 設置揮擊的目標位置
                double radians = Math.toRadians(attackAngle);
                double targetX = playerCenterX + (50 + distanceOffset) * Math.cos(radians) - meleeWeapon.getFitWidth() / 2;
                double targetY = playerCenterY + (50 + distanceOffset) * Math.sin(radians) - meleeWeapon.getFitHeight() / 2;
    
                meleeWeapon.setRotate(attackAngle - 45); //45 初始位置，揮擊從 -45 度開始
                meleeWeapon.setX(targetX);
                meleeWeapon.setY(targetY);
    
                // 動態模擬揮擊
                new Thread(() -> {
                    try {
                        // 前進到目標位置
                        for (int step = 0; step <= 10; step++) {
                            final double t = step / 10.0;
                            double radius = playerImage.getFitWidth() / 2 + 30; // 半徑距離，武器圍繞角色的距離
                            double curAngle = attackAngle - 45 + t * 90; // 計算當前旋轉角度
                        
                            // 計算當前武器相對角色中心的位置
                            double curX = playerImage.getX() + playerImage.getFitWidth() / 2 + radius * Math.cos(Math.toRadians(curAngle)) - meleeWeapon.getFitWidth() / 2;
                            double curY = playerImage.getY() + playerImage.getFitHeight() / 2 + radius * Math.sin(Math.toRadians(curAngle)) - meleeWeapon.getFitHeight() / 2;
                        
                            javafx.application.Platform.runLater(() -> {
                                meleeWeapon.setX(curX); // 更新武器的X位置
                                meleeWeapon.setY(curY); // 更新武器的Y位置
                                meleeWeapon.setRotate(curAngle); // 更新武器的旋轉角度
                                checkWeaponCollision(meleeWeapon, weaponDamagedEnemies, enemies); // 傳遞專屬集合                                
                            });
                            Thread.sleep(50); // 每50ms更新一次
                        }
    
                        javafx.application.Platform.runLater(() -> {
                            if (weaponIndex % 2 == 1) meleeWeapon.setScaleX(-1);
                            meleeWeapon.setRotate(originalAngle); // 恢復原始角度
                            animatedWeapons.remove(Integer.valueOf(weaponIndex));
                            positionWeapons(); // 恢復位置綁定
                            // damagedEnemies.clear(); // 攻擊完成後清空已受傷怪物集合
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        }
    }
    
    public void fireRangedWeapon(List<Enemy> enemies) {
    for (int i = 0; i < weapons.size(); i++) {
        if (!weaponTypes.get(i).equals("RANGED")) continue; // 僅對遠程武器進行發射動作

        ImageView rangedWeapon = weapons.get(i); // 當前遠程武器

        double weaponCenterX = rangedWeapon.getX() + rangedWeapon.getFitWidth() / 2;
        double weaponCenterY = rangedWeapon.getY() + rangedWeapon.getFitHeight() / 2;

        // 使用 AtomicReference 來保存最近敵人
        AtomicReference<Enemy> closestEnemyRef = new AtomicReference<>(null);
        AtomicReference<Double> closestDistanceRef = new AtomicReference<>(Double.MAX_VALUE);

        for (Enemy enemy : enemies) {
            double enemyCenterX = enemy.getImageView().getX() + enemy.getImageView().getFitWidth() / 2;
            double enemyCenterY = enemy.getImageView().getY() + enemy.getImageView().getFitHeight() / 2;

            double distance = Math.sqrt(
                Math.pow(weaponCenterX - enemyCenterX, 2) + Math.pow(weaponCenterY - enemyCenterY, 2)
            );

            if (distance < closestDistanceRef.get()) {
                closestDistanceRef.set(distance);
                closestEnemyRef.set(enemy);
            }
        }

        // 如果找到最近的敵人，發射子彈
        Enemy closestEnemy = closestEnemyRef.get();
        if (closestEnemy != null) {
            double enemyCenterX = closestEnemy.getImageView().getX() + closestEnemy.getImageView().getFitWidth() / 2;
            double enemyCenterY = closestEnemy.getImageView().getY() + closestEnemy.getImageView().getFitHeight() / 2;

            // 計算子彈飛行的角度
            double angle = Math.atan2(enemyCenterY - weaponCenterY, enemyCenterX - weaponCenterX);

            // 創建子彈
            ImageView bullet = new ImageView(new Image(BULLET_PATH));
            bullet.setFitWidth(100); // 調整子彈大小
            bullet.setFitHeight(100);
            bullet.setX(weaponCenterX - bullet.getFitWidth() / 2);
            bullet.setY(weaponCenterY - bullet.getFitHeight() / 2);
            bullet.setRotate(Math.toDegrees(angle)); // 設置子彈旋轉角度，對應方向

            root.getChildren().add(bullet); // 添加子彈到場景中

            AtomicBoolean hit = new AtomicBoolean(false); // 使用 AtomicBoolean

            // 動態模擬子彈飛行
            new Thread(() -> {
                try {
                    while (!hit.get()) {
                        javafx.application.Platform.runLater(() -> {
                            bullet.setX(bullet.getX() + 10 * Math.cos(angle)); // 子彈X方向移動
                            bullet.setY(bullet.getY() + 10 * Math.sin(angle)); // 子彈Y方向移動

                            // 檢查子彈是否擊中敵人
                            if (checkBulletCollision(bullet, closestEnemy)) {
                                closestEnemy.decreaseHealth(attackPower); // 扣除敵人血量
                                root.getChildren().remove(bullet); // 移除子彈
                                hit.set(true); // 設置為命中狀態
                            }

                            // 檢查子彈是否超出邊界
                            if (isOutOfBounds(bullet)) {
                                root.getChildren().remove(bullet); // 移除子彈
                                hit.set(true); // 終止飛行
                            }
                        });
                        Thread.sleep(20); // 每50ms更新一次
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}

    
    
    
    private boolean checkBulletCollision(ImageView bullet, Enemy enemy) {
        ImageView enemyImage = enemy.getImageView();
        double bulletX = bullet.getX();
        double bulletY = bullet.getY();
        double bulletWidth = bullet.getFitWidth();
        double bulletHeight = bullet.getFitHeight();
    
        double enemyX = enemyImage.getX();
        double enemyY = enemyImage.getY();
        double enemyWidth = enemyImage.getFitWidth();
        double enemyHeight = enemyImage.getFitHeight();
    
        // 判斷子彈是否擊中敵人
        return bulletX < enemyX + enemyWidth &&
               bulletX + bulletWidth > enemyX &&
               bulletY < enemyY + enemyHeight &&
               bulletY + bulletHeight > enemyY;
    }
    
    
    
    

    private void positionWeapons() {
        double centerX = playerImage.getX() + playerImage.getFitWidth() / 2;
    double centerY = playerImage.getY() + playerImage.getFitHeight() / 2;

    double radius = playerImage.getFitWidth() / 2 + 20; // 武器與角色的半徑距離

    for (int i = 0; i < weapons.size(); i++) {
        // 跳過正在執行動畫的武器
        if (animatedWeapons.contains(i)) continue;

        ImageView weapon = weapons.get(i);
        double angle;

        switch (i) {
            case 0: angle = 0; break;        // 右中
            case 1: angle = 180; break;     // 左中
            case 2: angle = 45; break;      // 右上
            case 3: angle = 135; break;     // 左上
            case 4: angle = -45; break;     // 右下
            case 5: angle = -135; break;    // 左下
            default: continue;
        }

        // 計算武器的位置
        double radians = Math.toRadians(angle);
        double weaponX = centerX + radius * Math.cos(radians) - weapon.getFitWidth() / 2;
        double weaponY = centerY + radius * Math.sin(radians) - weapon.getFitHeight() / 2;

        weapon.setX(weaponX);
        weapon.setY(weaponY);
    }
    }
    public boolean checkCollision(Object obj) {
        double objX, objY, objWidth, objHeight;
    
        if (obj instanceof Enemy) {
            Enemy enemy = (Enemy) obj;
            ImageView enemyImage = enemy.getImageView();
            objX = enemyImage.getX();
            objY = enemyImage.getY();
            objWidth = enemyImage.getFitWidth();
            objHeight = enemyImage.getFitHeight();
        } else if (obj instanceof ImageView) { // 假設子彈是 ImageView
            ImageView bullet = (ImageView) obj;
            objX = bullet.getX();
            objY = bullet.getY();
            objWidth = bullet.getFitWidth();
            objHeight = bullet.getFitHeight();
        } else {
            return false; // 不支持的對象類型
        }
    
        double playerX = playerImage.getX();
        double playerY = playerImage.getY();
        double playerWidth = playerImage.getFitWidth();
        double playerHeight = playerImage.getFitHeight();
    
        // 判斷兩個矩形是否相交（碰撞檢測）
        boolean isColliding = playerX < objX + objWidth &&
                              playerX + playerWidth > objX &&
                              playerY < objY + objHeight &&
                              playerY + playerHeight > objY;
    
        if (isColliding && !isCollisionCooldown) {
            isCollisionCooldown = true;
            decreaseHealth(); // 扣除生命值
    
            // 閃爍效果和冷卻機制
            new Thread(() -> {
                try {
                    long startTime = System.currentTimeMillis();
                    while (System.currentTimeMillis() - startTime < 1000) { // 冷卻持續 1 秒
                        javafx.application.Platform.runLater(() -> 
                            playerImage.setVisible(!playerImage.isVisible())
                        );
                        Thread.sleep(100); // 每次閃爍間隔 100ms
                    }
                    // 冷卻結束後恢復可見
                    javafx.application.Platform.runLater(() -> playerImage.setVisible(true));
                    isCollisionCooldown = false; // 重置冷卻狀態
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    
        return isColliding;
    }
    

    public void updateWeaponDirections(List<Enemy> enemies) {
        if (enemies.isEmpty()) return; // 如果沒有敵人，不更新武器方向
    
        for (int i = 0; i < weapons.size(); i++) {
            ImageView weapon = weapons.get(i);
    
            // 僅當武器類型為 "RANGED" 時執行瞄準
            if (!weaponTypes.get(i).equals("RANGED")) {
                continue;
            }
    
            Enemy closestEnemy = null;
            double closestDistance = Double.MAX_VALUE;
    
            double weaponCenterX = weapon.getX() + weapon.getFitWidth() / 2;
            double weaponCenterY = weapon.getY() + weapon.getFitHeight() / 2;
    
            for (Enemy enemy : enemies) {
                double enemyCenterX = enemy.getImageView().getX() + enemy.getImageView().getFitWidth() / 2;
                double enemyCenterY = enemy.getImageView().getY() + enemy.getImageView().getFitHeight() / 2;
    
                double distance = Math.sqrt(
                    Math.pow(weaponCenterX - enemyCenterX, 2) + Math.pow(weaponCenterY - enemyCenterY, 2)
                );
    
                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestEnemy = enemy;
                }
            }
    
            if (closestEnemy != null) {
                // 獲取敵人中心位置
                double enemyCenterX = closestEnemy.getImageView().getX() + closestEnemy.getImageView().getFitWidth() / 2;
                double enemyCenterY = closestEnemy.getImageView().getY() + closestEnemy.getImageView().getFitHeight() / 2;
    
                // 計算武器與敵人中心之間的角度
                double angle = Math.toDegrees(Math.atan2(enemyCenterY - weaponCenterY, enemyCenterX - weaponCenterX));
    
                // 確保槍枝保持“持正”，限制旋轉角度範圍為 -90 到 90 度
                if(i % 2 == 1)  
                {
                    // if (angle > 90 || angle < -90) {
                    //     angle += 180;
                    // }
                    weapon.setScaleX(1);
                }
    
                weapon.setRotate(angle); // 設置槍枝的旋轉角度
            }
        }
    }
    
    public void checkWeaponCollision(ImageView weapon, Set<Enemy> weaponDamagedEnemies, List<Enemy> enemies) {
        for (Enemy enemy : enemies) {
            if (weaponDamagedEnemies.contains(enemy)) continue; // 該武器已攻擊過此敵人
    
            ImageView enemyImage = enemy.getImageView();
            double weaponX = weapon.getX();
            double weaponY = weapon.getY();
            double weaponWidth = weapon.getFitWidth();
            double weaponHeight = weapon.getFitHeight();
    
            double enemyX = enemyImage.getX();
            double enemyY = enemyImage.getY();
            double enemyWidth = enemyImage.getFitWidth();
            double enemyHeight = enemyImage.getFitHeight();
    
            // 碰撞檢測
            boolean isColliding = weaponX < enemyX + enemyWidth &&
                                  weaponX + weaponWidth > enemyX &&
                                  weaponY < enemyY + enemyHeight &&
                                  weaponY + weaponHeight > enemyY;
    
            if (isColliding) {
                enemy.decreaseHealth(attackPower); // 減少敵人血量
                weaponDamagedEnemies.add(enemy); // 添加到該武器的已受傷集合
            }
        }
    }

    // 增加經驗值
    public void addExperience(int amount) {
        experience += amount;
        int requiredExp = 3 + 2 * (level - 1); // 每級所需經驗值公式
        if (experience >= requiredExp) {
            levelUp(); // 升級
        }
    }
    // 回復生命
    public void heal() {
        if (health < 10) {
            health+=3;
            System.out.println("Player healed! Current health: " + health);
        }
    }
    //增加攻擊力
    public void addAttackPower() {
        attackPower++;
        System.out.println("Attack power increased! Current power: " + attackPower);
    }

    // 升級方法
    private void levelUp() {
        experience = 0; // 重置經驗值
        level++; // 提升等級
        System.out.println("Leveled up! Current level: " + level);
        // 可在這裡增加其他升級邏輯（如增加攻擊力或生命值）
        isPausedForLevelUp = true;
    }

    // 獲取當前等級
    public int getLevel() {
        return level;
    }

    // 獲取當前經驗值比例（用於更新進度條）
    public double getExperienceProgress() {
        int requiredExp = 3 + 2 * (level - 1); // 每級所需經驗值公式
        return (double) experience / requiredExp;
    }
    

    private boolean isOutOfBounds(ImageView bullet) {
        return bullet.getX() < 0 || bullet.getY() < 0 ||
               bullet.getX() > mapWidth || bullet.getY() > mapHeight;
    }
    
    
    public void handleSpaceKeyAttack(List<Enemy> enemies) {
        attackMeleeWeapon(enemies); // 觸發近戰攻擊
        fireRangedWeapon(enemies); // 觸發遠程攻擊
    }

    private void updateWeaponPositions() {
        positionWeapons();
    }

    public List<ImageView> getWeapons() {
        return weapons;
    }
    
}
