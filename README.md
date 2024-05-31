Client-Server

Project Overview:
  The project aims to create a subscription service for users to receive updates about the World Cup through game channels.

Server (Java):
  Programmed in Java, the server incorporates features such as Thread-Per-Client (TPC) and Reactor server patterns for efficient client connection management.
  It seamlessly integrates the STOMP protocol for communication, maximizing resource utilization and throughput.
  Additionally, it employs asynchronous I/O operations to optimize performance.

Client (C++):
  Developed in C++, the client utilizes threads for concurrent handling of user interactions and communication with the server.
  It facilitates seamless communication with the server for subscribing to game channels and receiving World Cup updates.
