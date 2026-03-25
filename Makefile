# Makefile for WhereToNextUI
# Compile only
all:
	javac -cp .:gson-2.10.1.jar WhereToNextUI.java

# Run program
run: all
	java -cp .:gson-2.10.1.jar WhereToNextUI

# Clean compiled files
clean:
	rm -f *.class