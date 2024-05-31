CFLAGS:=-c -Wall -Weffc++ -g -std=c++11 -Iinclude
LDFLAGS:=-lboost_system -lpthread

all: clean StompWCIClient

EchoClient: bin/ConnectionHandler.o bin/echoClient.o
	g++ -o bin/EchoClient bin/ConnectionHandler.o bin/echoClient.o $(LDFLAGS)

StompWCIClient: bin/StompConnectionHandler.o bin/StompClient.o bin/event.o bin/Subscriptions.o bin/game.o
	g++ -o bin/StompWCIClient bin/StompConnectionHandler.o bin/StompClient.o bin/event.o bin/Subscriptions.o bin/game.o $(LDFLAGS)

bin/StompConnectionHandler.o: src/StompConnectionHandler.cpp
	g++ $(CFLAGS) -o bin/StompConnectionHandler.o src/StompConnectionHandler.cpp

bin/ConnectionHandler.o: src/ConnectionHandler.cpp
	g++ $(CFLAGS) -o bin/ConnectionHandler.o src/ConnectionHandler.cpp

bin/echoClient.o: src/echoClient.cpp
	g++ $(CFLAGS) -o bin/echoClient.o src/echoClient.cpp

bin/Subscriptions.o: src/Subscriptions.cpp
	g++ $(CFLAGS) -o bin/Subscriptions.o src/Subscriptions.cpp

bin/event.o: src/event.cpp
	g++ $(CFLAGS) -o bin/event.o src/event.cpp

bin/game.o: src/game.cpp
	g++ $(CFLAGS) -o bin/game.o src/game.cpp

bin/StompClient.o: src/StompClient.cpp
	g++ $(CFLAGS) -o bin/StompClient.o src/StompClient.cpp

.PHONY: clean
clean:
	rm -f bin/*
	