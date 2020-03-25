package com.github.bvigentas.grpc.greeting.client;

import com.proto.dummy.DummyServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GreetingClient {

    public static void main(String[] args) {
        System.out.println("Hello I'm a gRPC Client");

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .build();

        System.out.println("Creating Stub");
        DummyServiceGrpc.DummyServiceBlockingStub syncCliente = DummyServiceGrpc.newBlockingStub(channel);

        //DummyServiceGrpc.DummyServiceFutureStub asyncCliente = DummyServiceGrpc.newFutureStub(channel);

        //Do something

        System.out.println("Shutting down channel");
        channel.shutdown();
    }

}
