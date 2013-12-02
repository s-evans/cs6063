public class Record {
    public int runId;
    public DeathTask deathTask;
    public boolean alive;
    public Integer consensusValue;

    public Record (int runId, DeathTask deathTask, boolean alive, Integer consensusValue) {
        this.runId = runId;
        this.deathTask = deathTask;
        this.alive = alive;
        this.consensusValue = consensusValue;
    }
}