cd C:\Users\dfreelan\Documents\NetBeansProjects\bounties\Bounties\dist  

:: First Experiment doing nothing
java -classpath "C:\Users\dfreelan\Documents\NetBeansProjects\bounties\Bounties\lib" -jar Bounties.jar -for 200000 -repeat 25 -parallel 4 -prot 0 -agt 0 -die 0 -dir c:/dfreelan/SimpleExp1 
java -classpath "C:\Users\dfreelan\Documents\NetBeansProjects\bounties\Bounties\lib" -jar Bounties.jar -for 200000 -repeat 25 -parallel 4 -prot 0 -agt 1 -die 0 -dir c:/dfreelan/SimplePExp1 
java -classpath "C:\Users\dfreelan\Documents\NetBeansProjects\bounties\Bounties\lib" -jar Bounties.jar -for 200000 -repeat 25 -parallel 4 -prot 0 -agt 2 -die 0 -dir c:/dfreelan/SimpleRExp1 



java -classpath "C:\Users\dfreelan\Documents\NetBeansProjects\bounties\Bounties\lib" -jar Bounties.jar -for 200000 -repeat 25 -parallel 4 -prot 0 -agt 3 -die 0 -dir c:/dfreelan/ComplexExp1 
java -classpath "C:\Users\dfreelan\Documents\NetBeansProjects\bounties\Bounties\lib" -jar Bounties.jar -for 200000 -repeat 25 -parallel 4 -prot 0 -agt 4 -die 0 -dir c:/dfreelan/ComplexPExp1 
java -classpath "C:\Users\dfreelan\Documents\NetBeansProjects\bounties\Bounties\lib" -jar Bounties.jar -for 200000 -repeat 25 -parallel 4 -prot 0 -agt 5 -die 0 -dir c:/dfreelan/ComplexRExp1


java -classpath "C:\Users\dfreelan\Documents\NetBeansProjects\bounties\Bounties\lib" -jar Bounties.jar -for 200000 -repeat 25 -parallel 4 -prot 0 -agt 6 -die 0 -dir c:/dfreelan/RandomExp1
java -classpath "C:\Users\dfreelan\Documents\NetBeansProjects\bounties\Bounties\lib" -jar Bounties.jar -for 200000 -repeat 25 -parallel 4 -prot 0 -agt 7 -die 0 -dir c:/dfreelan/PseudoOptimalExp1


:: Second Experiment Dieing robots

java -classpath "C:\Users\dfreelan\Documents\NetBeansProjects\bounties\Bounties\lib" -jar Bounties.jar -for 200000 -repeat 25 -parallel 4 -prot 0 -agt 0 -die 1 -dir c:/dfreelan/SimpleExp2 
java -classpath "C:\Users\dfreelan\Documents\NetBeansProjects\bounties\Bounties\lib" -jar Bounties.jar -for 200000 -repeat 25 -parallel 4 -prot 0 -agt 1 -die 1 -dir c:/dfreelan/SimplePExp2 
java -classpath "C:\Users\dfreelan\Documents\NetBeansProjects\bounties\Bounties\lib" -jar Bounties.jar -for 200000 -repeat 25 -parallel 4 -prot 0 -agt 2 -die 1 -dir c:/dfreelan/SimpleRExp2 



java -classpath "C:\Users\dfreelan\Documents\NetBeansProjects\bounties\Bounties\lib" -jar Bounties.jar -for 200000 -repeat 25 -parallel 4 -prot 0 -agt 3 -die 1 -dir c:/dfreelan/ComplexExp2
java -classpath "C:\Users\dfreelan\Documents\NetBeansProjects\bounties\Bounties\lib" -jar Bounties.jar -for 200000 -repeat 25 -parallel 4 -prot 0 -agt 4 -die 1 -dir c:/dfreelan/ComplexPExp2 
java -classpath "C:\Users\dfreelan\Documents\NetBeansProjects\bounties\Bounties\lib" -jar Bounties.jar -for 200000 -repeat 25 -parallel 4 -prot 0 -agt 5 -die 1 -dir c:/dfreelan/ComplexRExp2


java -classpath "C:\Users\dfreelan\Documents\NetBeansProjects\bounties\Bounties\lib" -jar Bounties.jar -for 200000 -repeat 25 -parallel 4 -prot 0 -agt 6 -die 1 -dir c:/dfreelan/RandomExp2
java -classpath "C:\Users\dfreelan\Documents\NetBeansProjects\bounties\Bounties\lib" -jar Bounties.jar -for 200000 -repeat 25 -parallel 4 -prot 0 -agt 7 -die 1 -dir c:/dfreelan/PseudoOptimalExp2



:: Third Experiment rotating robots

java -classpath "C:\Users\dfreelan\Documents\NetBeansProjects\bounties\Bounties\lib" -jar Bounties.jar -for 200000 -repeat 25 -parallel 4 -prot 1 -agt 0 -die 0 -dir c:/dfreelan/SimpleExp3 
java -classpath "C:\Users\dfreelan\Documents\NetBeansProjects\bounties\Bounties\lib" -jar Bounties.jar -for 200000 -repeat 25 -parallel 4 -prot 1 -agt 1 -die 0 -dir c:/dfreelan/SimplePExp3 
java -classpath "C:\Users\dfreelan\Documents\NetBeansProjects\bounties\Bounties\lib" -jar Bounties.jar -for 200000 -repeat 25 -parallel 4 -prot 1 -agt 2 -die 0 -dir c:/dfreelan/SimpleRExp3



java -classpath "C:\Users\dfreelan\Documents\NetBeansProjects\bounties\Bounties\lib" -jar Bounties.jar -for 200000 -repeat 25 -parallel 4 -prot 1 -agt 3 -die 0 -dir c:/dfreelan/ComplexExp3 
java -classpath "C:\Users\dfreelan\Documents\NetBeansProjects\bounties\Bounties\lib" -jar Bounties.jar -for 200000 -repeat 25 -parallel 4 -prot 1 -agt 4 -die 0 -dir c:/dfreelan/ComplexPExp3 
java -classpath "C:\Users\dfreelan\Documents\NetBeansProjects\bounties\Bounties\lib" -jar Bounties.jar -for 200000 -repeat 25 -parallel 4 -prot 1 -agt 5 -die 0 -dir c:/dfreelan/ComplexRExp3


java -classpath "C:\Users\dfreelan\Documents\NetBeansProjects\bounties\Bounties\lib" -jar Bounties.jar -for 200000 -repeat 25 -parallel 4 -prot 1 -agt 6 -die 0 -dir c:/dfreelan/RandomExp3
java -classpath "C:\Users\dfreelan\Documents\NetBeansProjects\bounties\Bounties\lib" -jar Bounties.jar -for 200000 -repeat 25 -parallel 4 -prot 1 -agt 7 -die 0 -dir c:/dfreelan/PseudoOptimalExp3