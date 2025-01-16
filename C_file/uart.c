#include <xc.h>
    //setting TX/RX
int flag = 0;
char mystring[20];
int lenStr = 0;
int adc_result;
#define _XTAL_FREQ 4000000
//unsigned char rxComplete = 0; // ??????
void UART_Initialize() {
           
//          TODObasic   
//           Serial Setting      
//        1.   Setting Baud rate
//        2.   choose sync/async mode 
//        3.   enable Serial port (configures RX/DT and TX/CK pins as serial port pins)
//        3.5  enable Tx, Rx Interrupt(optional)
//        4.   Enable Tx & RX
           
    TRISCbits.TRISC6 = 1;            
    TRISCbits.TRISC7 = 1;            
    
    //  Setting baud rate
    TXSTAbits.SYNC = 0;           
    BAUDCONbits.BRG16 = 0;          
    TXSTAbits.BRGH =0 ;
    SPBRG = 12;      
    
   //   Serial enable
    RCSTAbits.SPEN = 1 ;      //enable asynchronous serial port        
    PIR1bits.TXIF = 1;  //set when txreg is empty
    PIR1bits.RCIF = 0;
    TXSTAbits.TXEN = 1;   //enable transmission        
    RCSTAbits.CREN = 1;             
    PIE1bits.TXIE = 0;    //set if interrupt is desired   
    IPR1bits.TXIP = 0;             
    PIE1bits.RCIE = 1;              
    IPR1bits.RCIP = 0;    
    INTCONbits.INT0IE = 1;
    INTCONbits.INT0IF = 0;
    RCONbits.IPEN = 0;
    }

void UART_Write(unsigned char data)  // Output on Terminal
{
    while(!TXSTAbits.TRMT);
    TXREG = data;              //write to TXREG will send data 
}


void UART_Write_Text(char* text) { // Output on Terminal, limit:10 chars
    for(int i=0;text[i]!='\0';i++)
        UART_Write(text[i]);
}

void ClearBuffer(){
    for(int i = 0; i < 10 ; i++)
        mystring[i] = '\0';
    lenStr = 0;
}

void MyusartRead()
{
   if (RCIF) { // ?????????????
        char received_char = RCREG; // ???????
        
        if (received_char == '\r') { // Enter ? (Carriage Return, '\r')
            UART_Write_Text("\r\n"); // ????
            mystring[lenStr] = '\0'; // ????
            //rxComplete = 1;          // ????????
            lenStr = 0;              // ??????
        } 
        else { 
            if (lenStr < 19) { // ??????
                mystring[lenStr++] = received_char; // ????
                UART_Write(received_char);          // ???????
            }
            else { // ??????
                UART_Write_Text("\r\nBuffer Full!\r\n");
                mystring[lenStr] = '\0'; // ????
                //rxComplete = 1;          // ??????
                lenStr = 0;              // ????
            }
        }
    }
}

char *GetString(){
    return mystring;
}
int Getlength(){
    return lenStr;
} 
int Getadc(){
    return adc_result;
} 
// void interrupt low_priority Lo_ISR(void)
void __interrupt(low_priority)  Lo_ISR(void)
{
    
    if(RCIF)
    {
        if(mystring[0]== 'm'&& mystring[1] =='2') flag =1;
        if(flag == 1) ClearBuffer();
        
        if(RCSTAbits.OERR)
        {
            CREN = 0;
            Nop();
            CREN = 1;
        }
        LATA = 0;
        
        MyusartRead();
    }
    if(INTCONbits.INT0IF){
        INTCONbits.INT0IF = 0;
        LATA = 0;
        ClearBuffer();
    }
//    if (PIR1bits.ADIF) { 
//        // ???????
//        adc_result = (ADRESH << 8) | ADRESL;
//        PIR1bits.ADIF = 0;
//
//        // ??????? A/D ??
//        __delay_us(100); 
//        ADCON0bits.GO = 1;
//    }
   // process other interrupt sources here, if required
    return;
}