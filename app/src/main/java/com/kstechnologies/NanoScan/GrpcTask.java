package com.kstechnologies.NanoScan;

import android.app.Activity;
import android.os.AsyncTask;

import com.kstechnologies.nirscannanolibrary.KSTNanoSDK;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.amaris.scan.AmarisServiceGrpc;
import io.grpc.amaris.scan.DictSchema;
import io.grpc.amaris.scan.ServerResponse;


public class GrpcTask extends AsyncTask<String, Void, String> {
    private final WeakReference<Activity> activityReference;
    private ManagedChannel channel;

    private String nameTs;
    private KSTNanoSDK.ScanResults scanResults;
    private ArrayList<String> dictlist;
    public GrpcTask(Activity activity, KSTNanoSDK.ScanResults scanResults, String nameTs, ArrayList<String> dict) {

        this.activityReference = new WeakReference<>(activity);
        this.scanResults = scanResults;
        this.nameTs = nameTs;
        this.dictlist = dict;
    }

    @Override
    protected String doInBackground(String... strings) {


        channel = ManagedChannelBuilder.forAddress("192.168.1.172", 7709)
                .usePlaintext(true)
                .build();

        AmarisServiceGrpc.AmarisServiceBlockingStub stub = AmarisServiceGrpc.newBlockingStub(channel);

        int grpcIndex;
        ArrayList<DictSchema.AmarisSchema> values = new ArrayList<>();
        for (grpcIndex = 0; grpcIndex < scanResults.getLength(); grpcIndex++) {
            DictSchema.AmarisSchema request = DictSchema.AmarisSchema.newBuilder()
                    .setWavelength(String.valueOf(scanResults.getWavelength()[grpcIndex]))
                    .setIntensity(String.valueOf(scanResults.getUncalibratedIntensity()[grpcIndex]))
                    .setAbsorbance(String.valueOf((-1) * (float) Math.log10((double) scanResults.getUncalibratedIntensity()[grpcIndex] / (double) scanResults.getIntensity()[grpcIndex])))
                    .setReflectance(String.valueOf((float) scanResults.getUncalibratedIntensity()[grpcIndex] / scanResults.getIntensity()[grpcIndex]))
                    .setNameTs(nameTs)
                    .build();
            values.add(request);
        }

        DictSchema dictSchema = DictSchema.newBuilder()
                .addAllSchema(values)
                .setScanType(dictlist.get(0))
                .setTimeStamp(dictlist.get(1))
                .setSpectStart(dictlist.get(2))
                .setSpectEnd(dictlist.get(3))
                .setNumPoints(dictlist.get(4))
                .setResolution(dictlist.get(5))
                .setNumAverages(dictlist.get(6))
                .setMeasTime(dictlist.get(7))
                .build();
        String message="failed";
        try {
            ServerResponse reply = stub.storeToELK(dictSchema);
            message = reply.getMessage();
        }catch (Exception e){
            System.out.println(GrpcTask.class.getName()+ message+ "76 line");
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
