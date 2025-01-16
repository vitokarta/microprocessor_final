#include "setting_hardaware/setting.h"
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include "usb_device.h"
#include "usb_device_hid.h"
#include "usb_descriptors.h"
#include "usb_helpers.h" // ?? USB ???????
#define _XTAL_FREQ 1000000

char str[20];

int x = 0, y = 0;


volatile unsigned char start_motor = 0;
int count = 0;

void __interrupt(high_priority) HighISR(void) {
    if (INT0IF) { 
        __delay_ms(30);
        start_motor = !start_motor; 
        INT0IF = 0;
    }
    else if(INT1IF){
        __delay_ms(30);
        count++;
        LATD = count;
        char buffer[20];
        sprintf(buffer, "count: %d\r\n", count);
        //UART_Write_Text(buffer);
        INT1IF = 0;
    }
//    else if(INT2IF){
//        //__delay_ms(30);
//        UART_Write_Text("space\r\n");
//        INT2IF = 0;
//    }
}
// ??????????
void Servo_Set_DutyCycle(unsigned char ccpr1l, unsigned char dc1b) {
    CCPR1L = ccpr1l;                    // ?? CCPR1L
    //CCP1CON = (CCP1CON & 0xCF) | (dc1b << 4); // ?? DC1B
    CCP1CONbits.DC1B = dc1b;
}


void main(void)
{
    char data[10], data1[10];
    SYSTEM_Initialize();
    
    // Timer2 -> On, prescaler -> 16
    T2CONbits.TMR2ON = 0b1;
    T2CONbits.T2CKPS = 0b10;
    
    PR2 = 249;

    
    CCPR1L = 23;          
    CCP1CONbits.DC1B = 0b10;
    
    
    TRISBbits.TRISB0 = 1; 
    TRISBbits.TRISB1 = 1;
    TRISBbits.TRISB2 = 1;
    
    TRISD = 0x00;
    LATD = 0x00;
    
    INTCON2bits.INTEDG0 = 0; 
    INTCONbits.INT0IE = 1; 
    INTCONbits.INT0IF = 0;  
    INTCON3bits.INT1IE = 1; 
    INTCON3bits.INT1IF = 0; 
//    INTCON3bits.INT2IE = 1; 
//    INTCON3bits.INT2IF = 0; 
    INTCONbits.GIE = 1;   
    
    unsigned char speed_stage = 0; // ????????0=???1=???2=??
    int state = 0;
    
    while (1) {
        // ?? Joystick ? X ? Y ??
        y = ADC_Read(0);  // Read X-axis
        x = ADC_Read(1);  // Read Y-axis
        
        sprintf(data, "X:%d", x);
        sprintf(data1, "Y:%d", y);
        UART_Write_Text(data);
        UART_Write_Text(" ");
        UART_Write_Text(data1);
        UART_Write_Text("\r\n");
        
        //__delay_ms(5);
        if (start_motor) { // ??????
            UART_Write_Text("botton pressed\r\n");
            int delay = 10;
            count = 0;
            for(int i = 29 ; i >= 28; i--){
                for(int j = 1; j >= 0; j--){
                    Servo_Set_DutyCycle(i, j);
                    for(int k = delay; k > 0; k--){
                        __delay_ms(50);
                    }
                    if(delay < 20){
                        delay++;
                    }
                }
            }
            state = (state + count) % 4;
            LATD = state;
            char buf[20];
            sprintf(buf, "%d\r\n", state);
            UART_Write_Text(buf);
            start_motor = 0;
        } else {
            Servo_Set_DutyCycle(23, 2); // ?????????
        }
    }
 
}