package com.github.bvigentas.grpc.greeting.client;

import com.proto.greet.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GreetingClient {

    ManagedChannel channel;

    public void run() {
        channel = ManagedChannelBuilder.forAddress("localhost", 50053)
                .usePlaintext()// Bypass on SSL
                .build();

        //doUnaryCall(channel);
        //doServerStreamingCall(channel);
        //doClientStreamingCall(channel);
        doBiDiStreamingCall(channel);

        System.out.println("Shutting down channel");
        channel.shutdown();

    }

    private void doBiDiStreamingCall(ManagedChannel channel) {
        GreetServiceGrpc.GreetServiceStub asyncClient = GreetServiceGrpc.newStub(channel);

        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<GreetEveryoneRequest> requestObserver = asyncClient.greetEveryone(new StreamObserver<GreetEveryoneResponse>() {
            @Override
            public void onNext(GreetEveryoneResponse value) {
                System.out.println("Response from server: " + value.getResult());
            }

            @Override
            public void onError(Throwable t) {
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                System.out.println("Server is done sending data");
                latch.countDown();
            }
        });

        Arrays.asList("Bruno", "Letícia").forEach( name -> {
            requestObserver.onNext(GreetEveryoneRequest.newBuilder()
                    .setGreeting(Greeting.newBuilder()
                        .setFirstName(name))
                    .build());
        });

        requestObserver.onCompleted();

        try {
            latch.await(3L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void doClientStreamingCall(ManagedChannel channel) {
        GreetServiceGrpc.GreetServiceStub asyncClient = GreetServiceGrpc.newStub(channel);

        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<LongGreetRequest> requestObserver =  asyncClient.longGreet(new StreamObserver<LongGreetResponse>() {
            @Override
            public void onNext(LongGreetResponse value) {
                //Got a response from the server
                System.out.println("Received a response from the server");
                System.out.println(value.getResult());
                //onNext will be called only once
            }

            @Override
            public void onError(Throwable t) {
                //Got a error from the server
            }

            @Override
            public void onCompleted() {
                //Server is done sending data.
                System.out.println("Server has completed sending us something");
                latch.countDown();
                //onCompleted will be called right after onNext
            }
        });

        System.out.println("Sending message 1");
        requestObserver.onNext(LongGreetRequest.newBuilder()
                .setGreting(Greeting.newBuilder().setFirstName("Bruno"))
                .build());

        System.out.println("Sending message 2");
        requestObserver.onNext(LongGreetRequest.newBuilder()
                .setGreting(Greeting.newBuilder().setFirstName("Letícia"))
                .build());

        //Tell server that client is done sending data
        requestObserver.onCompleted();

        try {
            latch.await(3L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void doServerStreamingCall(ManagedChannel channel) {
        GreetServiceGrpc.GreetServiceBlockingStub greetCliente = GreetServiceGrpc.newBlockingStub(channel);

        Greeting greeting = Greeting.newBuilder()
                .setFirstName("Bruno")
                .setLastName("Vigentas")
                .build();

        GreetManyTimesRequest request = GreetManyTimesRequest.newBuilder()
                .setGreeting(greeting)
                .build();

        greetCliente.greetManyTimes(request)
                .forEachRemaining(greetManyTimesResponse -> {
                    System.out.println(greetManyTimesResponse.getResult());
                });
    }

    private void doUnaryCall(ManagedChannel channel) {
        GreetServiceGrpc.GreetServiceBlockingStub greetCliente = GreetServiceGrpc.newBlockingStub(channel);

        Greeting greeting = Greeting.newBuilder()
                .setFirstName("Bruno")
                .setLastName("Vigentas")
                .build();

        GreetRequest greetRequest = GreetRequest.newBuilder()
                .setGreeting(greeting)
                .build();

        GreetResponse greetResponse = greetCliente.greet(greetRequest);
    }

    public static void main(String[] args) {

        GreetingClient main = new GreetingClient();
        main.run();
    }

}
