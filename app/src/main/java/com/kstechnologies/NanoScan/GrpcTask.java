package com.kstechnologies.NanoScan;

import android.app.Activity;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.amaris.scan.AmarisSchema;
import io.grpc.amaris.scan.AmarisServiceGrpc;
import io.grpc.amaris.scan.ServerResponse;


public  class GrpcTask extends AsyncTask<String, Void, String> {
    private final WeakReference<Activity> activityReference;
    private ManagedChannel channel;

    public GrpcTask(Activity activity){

        this.activityReference = new WeakReference<>(activity);
    }
    @Override
    protected String doInBackground(String... data) {

        channel = ManagedChannelBuilder.forAddress("192.168.1.35",7079).build();
        AmarisServiceGrpc.AmarisServiceBlockingStub stub = AmarisServiceGrpc.newBlockingStub(channel);
        AmarisSchema request = AmarisSchema.newBuilder()
                .setWavelength(data[0])
                .setIntensity(data[1])
                .setAbsorbance(data[2])
                .setReflectance(data[3])
                .build();

        ServerResponse reply = stub.storeToELK(request);

        return reply.getMessage();
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
