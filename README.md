# software-uart
this is a java software uart implemention.
 
# getting started
to use software-uart, the first thing that you need is a Trasceiver. fot that you need to implement `ir.kia.communication.uart.Transceiver`.
this interface contains only 2 methods, one for send data and one to recieve the data. and the values to send or receive is abinary value (0/1)

Next, create a new instance of `ir.kia.communication.uart.UART` and you are ready to go. you can use `write` method to send data from transceiver and check `in` variable for any inputs from transceiver.
 additionaly you can define a trigger while instantiating `UART` which will be triggered whenever a new data is available to read.
 `UART` use default serial configuration (8N1 @ 9600). If you need, you can define another `ir.kia.communication.uart.SerialConfig` and define it when constructing `UART` instance.
 
Do not forget to call the stop method at the end of you code, to stop threads responsible for reading and writing.

