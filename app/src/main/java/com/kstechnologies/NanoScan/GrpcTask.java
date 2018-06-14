package com.kstechnologies.NanoScan;

import android.app.Activity;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.amaris.scan.AmarisSchemaList;
import io.grpc.amaris.scan.AmarisServiceGrpc;
import io.grpc.amaris.scan.ServerResponse;


public class GrpcTask extends AsyncTask<String, Void, String> {
    private final WeakReference<Activity> activityReference;
    private ManagedChannel channel;

    private HashMap<Integer, ScanData> grpcData;

    public GrpcTask(Activity activity, HashMap<Integer, ScanData> grpcData ) {

        this.activityReference = new WeakReference<>(activity);
        this.grpcData = grpcData;
    }

    @Override
    protected String doInBackground(String... strings) {


        channel = ManagedChannelBuilder.forAddress("192.168.1.172", 7709).build();
        AmarisServiceGrpc.AmarisServiceBlockingStub stub = AmarisServiceGrpc.newBlockingStub(channel);
        AmarisSchemaList.AmarisSchema request;
        AmarisSchemaList list = null;
        String message = "";
        if (grpcData != null) {
            Set entrySet = grpcData.entrySet();
            Iterator it = entrySet.iterator();
            while (it.hasNext()) {
                Map.Entry map = (Map.Entry) it.next();
                ScanData data = (ScanData) map.getValue();
                 request = AmarisSchemaList.AmarisSchema.newBuilder()
                        .setWavelength(data.getWave())
                        .setIntensity(data.getIntensity())
                        .setAbsorbance(data.getAbsorb())
                        .setReflectance(data.getReflect())
                        .build();
                list = AmarisSchemaList.newBuilder()
                        .setSchema( (Integer) map.getKey(), request)
                        .build();

            }
            ServerResponse reply = stub.storeToELK(list);
            message = reply.getMessage();
        }

        return message;
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
