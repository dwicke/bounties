cd C:\Users\dfreelan\Documents\NetBeansProjects\bounties\Bounties\dist  

:: First Experiment doing nothing
java -classpath "C:\Users\dfreelan\Documents\NetBeansProjects\bounties\Bounties\lib" -jar Bounties.jar -for 200000 -repeat 25 -parallel 4 -prot 0 -agt 9 -die 0 -dir c:/dfreelan/exp1/SimpleExclu
java -classpath "C:\Users\dfreelan\Documents\NetBeansProjects\bounties\Bounties\lib" -jar Bounties.jar -for 200000 -repeat 25 -parallel 4 -prot 0 -agt 8 -die 0 -dir c:/dfreelan/exp1/SeanAuction
java -classpath "C:\Users\dfreelan\Documents\NetBeansProjects\bounties\Bounties\lib" -jar Bounties.jar -for 200000 -repeat 25 -parallel 4 -prot 0 -agt 1 -die 0 -dir c:/dfreelan/exp1/SimpleP

:: Second Experiment Dieing robots

java -classpath "C:\Users\dfreelan\Documents\NetBeansProjects\bounties\Bounties\lib" -jar Bounties.jar -for 200000 -repeat 25 -parallel 4 -prot 0 -agt 9 -die 1 -dir c:/dfreelan/exp2/SimpleExclu
java -classpath "C:\Users\dfreelan\Documents\NetBeansProjects\bounties\Bounties\lib" -jar Bounties.jar -for 200000 -repeat 25 -parallel 4 -prot 0 -agt 8 -die 1 -dir c:/dfreelan/exp2/SeanAuction
java -classpath "C:\Users\dfreelan\Documents\NetBeansProjects\bounties\Bounties\lib" -jar Bounties.jar -for 200000 -repeat 25 -parallel 4 -prot 0 -agt 1 -die 1 -dir c:/dfreelan/exp2/SimpleP
:: Third Experiment rotating robots

java -classpath "C:\Users\dfreelan\Documents\NetBeansProjects\bounties\Bounties\lib" -jar Bounties.jar -for 200000 -repeat 25 -parallel 4 -prot 1 -agt 9 -die 0 -dir c:/dfreelan/exp3/SimpleExclu
java -classpath "C:\Users\dfreelan\Documents\NetBeansProjects\bounties\Bounties\lib" -jar Bounties.jar -for 200000 -repeat 25 -parallel 4 -prot 1 -agt 8 -die 0 -dir c:/dfreelan/exp3/SeanAuction
java -classpath "C:\Users\dfreelan\Documents\NetBeansProjects\bounties\Bounties\lib" -jar Bounties.jar -for 200000 -repeat 25 -parallel 4 -prot 1 -agt 1 -die 0 -dir c:/dfreelan/exp3/SimpleP