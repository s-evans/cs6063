public class Record {
    public int runId;
    public DeathTask deathTask;
    public boolean alive;
    public int consensusValue;

    public Record (int runId, DeathTask deathTask, boolean alive, int consensusValue) {
        this.runId = runId;
        this.deathTask = deathTask;
        this.alive = alive;
        this.consensusValue = consensusValue;
    }
}