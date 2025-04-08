package edu.yu.introtoalgs;

import edu.yu.introtoalgs.OctopusCountI.*;

import org.junit.Test;

import java.util.*;

import static edu.yu.introtoalgs.OctopusCountI.ArmColor.*;
import static edu.yu.introtoalgs.OctopusCountI.ArmTexture.*;
import static org.junit.Assert.*;

public class OctopusCountTest {


    @Test
    public void addObservationTestWith2() {
        long startTime = System.currentTimeMillis();
        OctopusCount octCount = new OctopusCount();

        ArmColor[] colors1= {GRAY,GRAY,GRAY,RED,RED,RED,BLACK,BLACK};
        int[] lengthInCM1 = {1,2,3,4,5,6,7,8};
        ArmTexture[] textures1 = {SMOOTH,SMOOTH,SMOOTH,SLIMY,SLIMY,SLIMY,STICKY,STICKY};

        ArmColor[] colors2= {BLACK,RED,GRAY,GRAY,RED,GRAY,BLACK,RED};
        int lengthInCM2[] = {2,3,4,5,6,7,8,9};
        ArmTexture[] textures2 = {STICKY,SLIMY,SMOOTH,SMOOTH,SLIMY,SMOOTH,STICKY,SLIMY};

        ArmColor[] colors3= {RED,GRAY,GRAY,RED,RED,BLACK,GRAY,BLACK};
        int lengthInCM3[] = {5,2,1,6,4,8,3,7};
        ArmTexture[] textures3 = {SLIMY,SMOOTH,SMOOTH,SLIMY,SLIMY,STICKY,SMOOTH,STICKY};


        octCount.addObservation(1,colors1,lengthInCM1,textures1);
        octCount.addObservation(2,colors2,lengthInCM2,textures2);
        octCount.addObservation(3,colors1,lengthInCM1,textures1);
        octCount.addObservation(4,colors3,lengthInCM3,textures3);
        assertEquals(2,octCount.countThem());
        long endTime = System.currentTimeMillis();
        //System.out.println("addObservationTestWith2 took " + (endTime - startTime) + " milliseconds");
        //Average time took to run = 0 miliseconds (ran 100000 times)
    }

    @Test
    public void oneObervationMultipleTimes(){
        long startTime = System.currentTimeMillis();
        OctopusCount octCount = new OctopusCount();

        ArmColor[] colors1= {GRAY,GRAY,GRAY,RED,RED,RED,BLACK,BLACK};
        int[] lengthInCM1 = {1,2,3,4,5,6,7,8};
        ArmTexture[] textures1 = {SMOOTH,SMOOTH,SMOOTH,SLIMY,SLIMY,SLIMY,STICKY,STICKY};

        octCount.addObservation(0,colors1,lengthInCM1,textures1);

        int amount = 1000;
        for(int i = 0; i < amount; i++){
            shuffleArrays(colors1,lengthInCM1,textures1);
            octCount.addObservation(i+1,colors1,lengthInCM1,textures1);
        }

        assertEquals(1,octCount.countThem());

        long endTime = System.currentTimeMillis();
        //System.out.println("oneObervationMultipleTimes took " + (endTime - startTime) + " milliseconds");
        //Note: Average = ran 1000 times
        //Average when amount = 100 is 0 milliseconds
        //Average when amount = 1000 is 1 milliseconds
        //Average when amount = 10000 is 9 milliseconds
        //Average when amount = 100000 is 102 milliseconds
    }

    @Test
    public void runTestMultipletimes(){
        long totalTime = 0;
        int N = 1000;
        for(int i = 0; i < N; i++){
            long startTime = System.currentTimeMillis();
            //addObservationTestWith2();
            oneObervationMultipleTimes();
            //n1000Observations();
            //NOTuniqueObservations();
            //uniqueObservations();
            long endTime = System.currentTimeMillis();
            totalTime += (endTime - startTime);
        }
        //System.out.println("When N = " + N + " uniqueObservations took an average of " + (totalTime/N) + " milliseconds");
    }



    @Test
    public void n1000Observations(){
        long startTime = System.currentTimeMillis();
        OctopusCount octCount = new OctopusCount();
        int amount = 1000;
        for(int i = 0; i < amount; i++){
            ArmColor[] colors = new ArmColor[8];
            int[] lengthInCM = new int[8];
            ArmTexture[] textures = new ArmTexture[8];
            for(int j = 0; j < 8; j++){
                colors[j] = ArmColor.values()[(int)(Math.random()*3)];
                lengthInCM[j] = (int)(Math.random()*1000);
                textures[j] = ArmTexture.values()[(int)(Math.random()*3)];
            }
            octCount.addObservation(i,colors,lengthInCM,textures);
        }

        long endTime = System.currentTimeMillis();
        //System.out.println("n1000Observations took " + (endTime - startTime) + " milliseconds");
        //Note: Average = ran 1000 times
        //Average when amount = 100 is 0 milliseconds
        //Average when amount = 1000 is 1 milliseconds
        //Average when amount = 10000 is 15 milliseconds
        //Average when amount = 100000 is 183 milliseconds
    }

    @Test
    public void uniqueObservations(){
        long startTime = System.currentTimeMillis();

        OctopusCount octCount = new OctopusCount();
        Set<String> uniqueObservations = new HashSet<>();
        int totalObservations = 0;
        int observationID = 0;

        while (uniqueObservations.size() < 10000) {
            ArmColor[] colors = new ArmColor[8];
            int[] lengthInCM = new int[8];
            ArmTexture[] textures = new ArmTexture[8];

            List<OctArm> octArms = new LinkedList<>();
            for (int j = 0; j < 8; j++) {
                colors[j] = ArmColor.values()[(int) (Math.random() * 3)];
                lengthInCM[j] = (int) (Math.random() * 100);
                textures[j] = ArmTexture.values()[(int) (Math.random() * 3)];
                octArms.add(new OctArm(j, colors[j], lengthInCM[j], textures[j]));
            }
            Collections.sort(octArms);
            octCount.addObservation(observationID, colors, lengthInCM, textures);
            observationID++;
            //System.out.println(octArms.toString());
            if(uniqueObservations.add(octArms.toString())){
                totalObservations++;
            }

        }
        assertEquals(totalObservations,octCount.countThem());
        assertEquals(uniqueObservations.size(),octCount.countThem());

        long endTime = System.currentTimeMillis();
        //System.out.println("uniqueObservations() took " + (endTime - startTime) + " milliseconds");
        //When run 1000 times took an average of 25 milliseconds
    }

    @Test
    public void NOTuniqueObservations(){
        long startTime = System.currentTimeMillis();

        OctopusCount octCount = new OctopusCount();
        Set<String> uniqueObservations = new HashSet<>();
        int totalObservations = 0;
        int observationID = 0;

        while (totalObservations < 10000) {
            ArmColor[] colors = new ArmColor[8];
            int[] lengthInCM = new int[8];
            ArmTexture[] textures = new ArmTexture[8];

            List<OctArm> octArms = new LinkedList<>();
            for (int j = 0; j < 8; j++) {
                colors[j] = ArmColor.values()[(int) (Math.random() * 3)];
                lengthInCM[j] = (int) (Math.random() * 2);
                textures[j] = ArmTexture.values()[(int) (Math.random() * 3)];
                octArms.add(new OctArm(observationID, colors[j], lengthInCM[j], textures[j]));
            }
            Collections.sort(octArms);
            octCount.addObservation(observationID, colors, lengthInCM, textures);
            observationID++;
            //System.out.println(octArms.toString());
            if(uniqueObservations.add(octArms.toString())){
                totalObservations++;
            }

        }
        assertEquals(uniqueObservations.size(),octCount.countThem());
        long endTime = System.currentTimeMillis();
        //System.out.println("NOTuniqueObservations took " + (endTime - startTime) + " milliseconds");
        //when ran 1000 times average time = 20 milliseconds
    }

    @Test
    public void throwsIAEtoLittleLegs(){
        OctopusCount octCont = new OctopusCount();
        ArmColor[] colors1= {GRAY,GRAY,RED,RED,RED,BLACK,BLACK};//7
        int[] lengthInCM1 = {1,2,3,4,5,6,7,8};//8
        ArmTexture[] textures1 = {SMOOTH,SMOOTH,SMOOTH,SLIMY,SLIMY,SLIMY,STICKY,STICKY};//8
        //assert that it throws IAE because there aren't exactly N_ARMS values for each arm characteristic
        assertThrows(IllegalArgumentException.class, () -> octCont.addObservation(0,colors1,lengthInCM1,textures1));
        ArmColor[] colors2= {GRAY,RED,GRAY,RED,RED,RED,BLACK,BLACK};//8
        int[] lengthInCM2 = {1,2,3,4,5,6,7};//7
        assertThrows(IllegalArgumentException.class, () -> octCont.addObservation(1,colors2,lengthInCM2,textures1));
        ArmTexture[] textures2 = {SMOOTH,SMOOTH,SMOOTH,SLIMY,SLIMY,SLIMY,STICKY};//7
        assertThrows(IllegalArgumentException.class, () -> octCont.addObservation(2,colors2,lengthInCM1,textures2));

    }

    @Test
    public void throwsIAEwhenNegative(){
        OctopusCount octCont = new OctopusCount();
        ArmColor[] colors1= {GRAY,GRAY,GRAY,RED,RED,RED,BLACK,BLACK};
        int[] lengthInCM1 = {1,2,3,4,5,6,7,8};
        ArmTexture[] textures1 = {SMOOTH,SMOOTH,SMOOTH,SLIMY,SLIMY,SLIMY,STICKY,STICKY};
        //assert that it throws IAE because observationId is not a non-negative integer
        assertThrows(IllegalArgumentException.class, () -> octCont.addObservation(-1,colors1,lengthInCM1,textures1));
        int lengthInCM2[] = {2,3,4,5,-6,7,8,9};
        //assert that it throws IAE because lengthInCM value is not a positive integer
        assertThrows(IllegalArgumentException.class, () -> octCont.addObservation(2,colors1,lengthInCM2,textures1));
    }

    @Test
    public void throwsIAEwhenTwoObservations(){
        OctopusCount octCont = new OctopusCount();
        ArmColor[] colors1= {GRAY,GRAY,GRAY,RED,RED,RED,BLACK,BLACK};
        int[] lengthInCM1 = {1,2,3,4,5,6,7,8};
        ArmTexture[] textures1 = {SMOOTH,SMOOTH,SMOOTH,SLIMY,SLIMY,SLIMY,STICKY,STICKY};
        octCont.addObservation(0,colors1,lengthInCM1,textures1);
        //assert that it throws IAE because observationId is not unique
        assertThrows(IllegalArgumentException.class, () -> octCont.addObservation(0,colors1,lengthInCM1,textures1));
    }

    private class OctArm implements Comparable<OctArm>{
        private ArmColor color;
        private int lengthInCM;
        private ArmTexture texture;
        private int observationId;
        public OctArm(int observationId, ArmColor color, int lengthInCM, ArmTexture texture){
            this.color = color;
            this.lengthInCM = lengthInCM;
            this.texture = texture;
            this.observationId = observationId;
        }

        public ArmColor getColor(){
            return this.color;
        }
        public int getLengthInCM(){
            return this.lengthInCM;
        }
        public ArmTexture getTexture(){
            return this.texture;
        }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            OctArm octArmNode = (OctArm) o;
            return lengthInCM == octArmNode.lengthInCM && color == octArmNode.color && texture == octArmNode.texture;
        }

        @Override
        public int hashCode() {
            return Objects.hash(color, lengthInCM, texture);
        }

        @Override
        public int compareTo(OctArm o) {
            return Integer.compare(this.lengthInCM, o.lengthInCM);
        }

        @Override
        public String toString() {
            return (""+this.lengthInCM+this.color+this.texture);
        }
    }

    public void shuffleArrays(ArmColor[] arr1, int[] arr2, ArmTexture[] arr3) {
        if (arr1.length != arr2.length || arr2.length != arr3.length) {
            throw new IllegalArgumentException("Input arrays must have the same length.");
        }

        int[] indices = generateShuffledIndices(arr1.length);

        shuffleArray(arr1, indices);
        shuffleArray(arr2, indices);
        shuffleArray(arr3, indices);
    }

    public int[] generateShuffledIndices(int length) {
        int[] indices = new int[length];
        for (int i = 0; i < length; i++) {
            indices[i] = i;
        }

        Random random = new Random();
        for (int i = length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            // Swap indices[i] and indices[j]
            int temp = indices[i];
            indices[i] = indices[j];
            indices[j] = temp;
        }

        return indices;
    }

    public <T> void shuffleArray(T[] arr, int[] indices) {
        T[] copy = Arrays.copyOf(arr, arr.length);
        for (int i = 0; i < arr.length; i++) {
            arr[i] = copy[indices[i]];
        }
    }

    public  void shuffleArray(int[] arr, int[] indices) {
        int[] copy = Arrays.copyOf(arr, arr.length);
        for (int i = 0; i < arr.length; i++) {
            arr[i] = copy[indices[i]];
        }
    }


}
