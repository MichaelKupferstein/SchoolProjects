package edu.yu.introtoalgs;

import java.util.*;

public class OctopusCount implements OctopusCountI{


    private Set<String> octopusHashes;
    private Set<Integer> observationIds;
    public OctopusCount(){
       this.octopusHashes= new HashSet<>();
       this.observationIds = new HashSet<>();
    }
    /**
     * A single octopus observation, consisting of a set of arrays (each of size
     * exactly N_ARMS), such that the ith element of each array describes the
     * characteristics of the ith arm of the observed octopus.
     *
     * @param observationId non-negative integer, uniquely labels the observation
     *                      (multiple observations can map to the same octopus),
     * @param colors        the color of the ith arm, not null, elements can't be null
     * @param lengthInCM    the length of the ith arm, not null, elements must be
     *                      positive integers
     * @param textures      the texture of the ith arm, not null, elements can't be
     *                      null
     * @throws IllegalArgumentException if any of the parameter conditions are
     *                                  violated: e.g., there aren't exactly N_ARMS values for each arm
     *                                  characteristic or if a lengthInCM value is not a positive integer.
     */
    @Override
    public void addObservation(int observationId, ArmColor[] colors, int[] lengthInCM, ArmTexture[] textures) {
        if(colors.length != N_ARMS || lengthInCM.length != N_ARMS || textures.length != N_ARMS){
            throw new IllegalArgumentException("There aren't exactly N_ARMS values for each arm characteristic");
        }
        if(observationId < 0){
            throw new IllegalArgumentException("observationId is not a non-negative integer");
        }
        if(this.observationIds.contains(observationId)){
            throw new IllegalArgumentException("observationId is not unique");
        }
        OctArm[] octArms = new OctArm[N_ARMS];
        for(int i = 0; i < N_ARMS; i++){
            if(lengthInCM[i] < 0){
                throw new IllegalArgumentException("lengthInCM value is not a positive integer");
            }
            octArms[i] = new OctArm(observationId,colors[i], lengthInCM[i], textures[i]);
        }
        Arrays.sort(octArms);
        String hash = createOctopusHashID(octArms);
        this.octopusHashes.add(hash);
        this.observationIds.add(observationId);
    }

    private String createOctopusHashID(OctArm[] arms){
        //create a hashcode for the octopus based on the hashcode of each arm
        String hash = "";
        for(OctArm arm : arms){
            hash += arm.toString();
        }
        return hash;
    }


    /**
     * Returns the number of unique octopus instances from the set of current
     * observations.
     *
     * @return the number of unique instances.
     */
    @Override
    public int countThem() {
        return this.octopusHashes.size();
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

}
