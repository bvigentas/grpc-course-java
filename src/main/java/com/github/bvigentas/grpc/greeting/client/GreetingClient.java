package com.github.bvigentas.grpc.greeting.client;

import com.proto.greet.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GreetingClient {

    public static void main(String[] args) {
        System.out.println("Hello I'm a gRPC Client");

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50053)
                .usePlaintext()// Bypass on SSL
                .build();

        System.out.println("Creating Stub");

        //Create greet service client
        GreetServiceGrpc.GreetServiceBlockingStub greetCliente = GreetServiceGrpc.newBlockingStub(channel);

        //Created proto buff greeting message
        Greeting greeting = Greeting.newBuilder()
                .setFirstName("Bruno")
                .setLastName("Vigentas")
                .build();

        /*
        // UNARY
        //Create proto buff request
        GreetRequest greetRequest = GreetRequest.newBuilder()
                .setGreeting(greeting)
                .build();

        //Call RPC and get response (proto buff)
        GreetResponse greetResponse = greetCliente.greet(greetRequest);

        System.out.println(greetResponse.getResult());
        */


        // SERVER STREAMING

        GreetManyTimesRequest request = GreetManyTimesRequest.newBuilder()
                .setGreeting(greeting)
                .build();

        greetCliente.greetManyTimes(request)
                .forEachRemaining(greetManyTimesResponse -> {
                    System.out.println(greetManyTimesResponse.getResult());
                });


        System.out.println("Shutting down channel");
        channel.shutdown();
    }

}
