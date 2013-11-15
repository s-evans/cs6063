
public class Record {
    public int runId;
    public DeathTask deathTask;
    public boolean alive;

    public Record (int runId, DeathTask deathTask, boolean alive) {
        this.runId = runId;
        this.deathTask = deathTask;
        this.alive = alive;
    }
}