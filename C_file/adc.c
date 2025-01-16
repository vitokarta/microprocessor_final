#include <xc.h>
#define _XTAL_FREQ 4000000
void ADC_Initialize(void) {
    //TRISA = 0x01;		// Set as input port
    ADCON1 = 0x0e;  	// Ref vtg is VDD & Configure pin as analog pin 
    // ADCON2 = 0x92;  	
//    ADCON1 = 0x0E; // ?? AN0 ? AN1 ???????????
    TRISA = 0x03;
   
    ADFM = 1 ;          // Right Justifie
    ADCON2bits.ADCS = 7; // 
    //ADCON2 = 0x94;
    ADRESH=0;  			// Flush ADC output Register
    ADRESL=0;  
}

int ADC_Read(int channel)
{
    int digital;
    
    ADCON0bits.CHS =  channel;; // Select Channe7
    
    //__delay_us(10);  
    ADCON0bits.GO = 1;
    ADCON0bits.ADON = 1;
    
    while(ADCON0bits.GO_nDONE==1);

    digital = (ADRESH*256) | (ADRESL);
    //digital = (ADRESH << 8 )^ADRESL;
    return(digital);
}