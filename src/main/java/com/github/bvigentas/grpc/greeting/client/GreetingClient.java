package com.github.bvigentas.grpc.greeting.client;

import com.proto.dummy.DummyServiceGrpc;
import com.proto.greet.GreetRequest;
import com.proto.greet.GreetResponse;
import com.proto.greet.GreetServiceGrpc;
import com.proto.greet.Greeting;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GreetingClient {

    public static void main(String[] args) {
        System.out.println("Hello I'm a gRPC Client");

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
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

        //Create proto buff request
        GreetRequest greetRequest = GreetRequest.newBuilder()
                .setGreeting(greeting)
                .build();

        //Call RPC and get response (proto buff)
        GreetResponse greetResponse = greetCliente.greet(greetRequest);

        System.out.println(greetResponse.getResult());

        //Do something

        System.out.println("Shutting down channel");
        channel.shutdown();
    }

}
