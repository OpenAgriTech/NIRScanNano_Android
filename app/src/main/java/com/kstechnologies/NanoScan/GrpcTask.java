package com.kstechnologies.NanoScan;

import android.app.Activity;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannel;

public  class GrpcTask extends AsyncTask<String, Void, String> {
    private final WeakReference<Activity> activityReference;
    private ManagedChannel channel;

    public GrpcTask(Activity activity){

        this.activityReference = new WeakReference<Activity>(activity);
    }
    @Override
    protected String doInBackground(String... strings) {

//        channel = ManagedChannelBuilder.forAddress("localhost",7079).build();
//        GreeterGrpc.GreeterBlockingStub stub = GreeterGrpc.newBlockingStub(channel);
//        HelloRequest request = HelloRequest.newBuilder().setName("amaris").build();
//
//        HelloReply reply = stub.sayHello(request);stub.sayHello(request);
        //Todo
        // new api for grpc
        //CSV.schema sendServer(proto)
//        GrpcTask gt = new GrpcTask(this);
//        ArrayList<String> array = new ArrayList<>();
//        gt.doInBackground((String[]) array.toArray());
        return "";//reply.getMessage();
    }

    @Override
    protected void onPostExecute(String result) {
        try {
            channel.shutdown().awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        Activity activity = activityReference.get();
        if (activity == null) {
            return;
        }
    }
}
