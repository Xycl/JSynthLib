CD ..
FOR /L %%A IN (1,1,10) DO (
  mvn clean compile test > test_log_%%A.txt
)