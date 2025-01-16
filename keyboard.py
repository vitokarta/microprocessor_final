import serial
from pynput.keyboard import Controller
import time

# 初始化鍵盤控制器
keyboard = Controller()

# 設定串口參數（根據你的 UART 端口調整）
ser = serial.Serial(
    port='COM7',         # 更改為你的串口名稱，例如 "COM3" 或 "/dev/ttyUSB0"
    baudrate=1200,       # 與 PIC 設定一致
    bytesize=serial.EIGHTBITS,
    parity=serial.PARITY_NONE,
    stopbits=serial.STOPBITS_ONE,
    timeout=1            # 設定超時，避免阻塞
)

print("Listening to UART...")

def simulate_key(x, y):
    if y > 700 and x < 300:
        print("Key: W + A (Up Left)")
        keyboard.press('w')
        keyboard.press('a')
        time.sleep(0.1)
        keyboard.release('w')
        keyboard.release('a')
    elif y > 700 and x > 700:
        print("Key: W + D (Up Right)")
        keyboard.press('w')
        keyboard.press('d')
        time.sleep(0.1)
        keyboard.release('w')
        keyboard.release('d')
    elif y < 300 and x < 300:
        print("Key: S + A (Down Left)")
        keyboard.press('s')
        keyboard.press('a')
        time.sleep(0.1)
        keyboard.release('s')
        keyboard.release('a')
    elif y < 300 and x > 700:
        print("Key: S + D (Down Right)")
        keyboard.press('s')
        keyboard.press('d')
        time.sleep(0.1)
        keyboard.release('s')
        keyboard.release('d')
    elif y > 700:
        print("Key: W (Up)")
        keyboard.press('w')
        time.sleep(0.1)
        keyboard.release('w')
    elif y < 300:
        print("Key: S (Down)")
        keyboard.press('s')
        time.sleep(0.1)
        keyboard.release('s')
    elif x > 700:
        print("Key: D (Right)")
        keyboard.press('d')
        time.sleep(0.1)
        keyboard.release('d')
    elif x < 300:
        print("Key: A (Left)")
        keyboard.press('a')
        time.sleep(0.1)
        keyboard.release('a')
    else:
        print("Key: None")

# 處理數字按鍵模擬
def simulate_number_key(data):
    if data == "0":
        print("Key: 1")
        keyboard.press('1')
        time.sleep(0.1)
        keyboard.release('1')
    elif data == "1":
        print("Key: 2")
        keyboard.press('2')
        time.sleep(0.1)
        keyboard.release('2')
    elif data == "2":
        print("Key: 3")
        keyboard.press('3')
        time.sleep(0.1)
        keyboard.release('3')
    elif data == "3":
        print("Key: 4")
        keyboard.press('4')
        time.sleep(0.1)
        keyboard.release('4')

# 持續從 UART 讀取資料
while True:
    if ser.in_waiting > 0:  # 檢查是否有資料進來
        line = ser.readline().decode('utf-8').strip()  # 讀取 UART 資料
        print(f"Received: {line}")

        # 解析資料
        if line.startswith("X:") and "Y:" in line:
            try:
                # 提取 X 和 Y 的值
                parts = line.split(" ")
                x = int(parts[0].split(":")[1])
                y = int(parts[1].split(":")[1])
                print(f"X={x}, Y={y}")

                # 模擬鍵盤輸入
                simulate_key(x, y)

            except ValueError:
                print("Error parsing data:", line)
        
        # 處理數字按鍵輸入
        elif line in ["0", "1", "2", "3"]:
            simulate_number_key(line)






# # 開啟 UART 連接（請根據你的 USB-to-Serial 模組設定適當的端口）
# ser = serial.Serial('COM3', 9600)  # 修改 'COM3' 為你的串口名稱

# while True:
#     if ser.in_waiting > 0:  # 檢查是否有資料傳入
#         data = ser.readline().decode().strip()  # 讀取一行資料並去除換行符號
#         print(f"Received: {data}")  # 調試用，顯示接收到的資料

#         # 根據接收到的資料模擬按鍵輸入
#         if "X:0" in data and "Y:700" in data:
#             keyboard.press('w')
#             keyboard.release('w')
#         elif "X:0" in data and "Y:300" in data:
#             keyboard.press('s')
#             keyboard.release('s')
#         elif "X:700" in data and "Y:0" in data:
#             keyboard.press('d')
#             keyboard.release('d')
#         elif "X:300" in data and "Y:0" in data:
#             keyboard.press('a')
#             keyboard.release('a')
#         else:
#             print("No valid direction detected.")
# cun=5
# while cun > 0:
#     keyboard.press('w')
#     keyboard.release('w')
#     print("Pressed 'w'")  # 調試用，顯示按鍵操作
#     time.sleep(2)  # 延遲2秒
#     cun -= 1