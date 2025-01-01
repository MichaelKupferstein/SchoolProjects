#!/bin/bash

# Step 1: Build your code using mvn test
mvn test

# Step 2: Create a cluster of 7 peer servers and one gateway, starting each in their own JVM

# Step 3: Wait until the election has completed before sending any requests to the Gateway

# Step 4: Send 9 client requests and print the responses

# Step 5: Kill a follower JVM and wait for failure detection

# Step 6: Kill the leader JVM and send 9 more client requests

# Step 7: Wait for the Gateway to have a new leader and print the node ID of the leader

# Step 8: Send/display 1 more client request (in the foreground) and print the response

# Step 9: List the paths to files containing the Gossip messages received by each node

# Step 10: Shut down all the nodes
