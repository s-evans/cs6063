public class Record {
    public int runId;
    public DeathTask deathTask;
    public boolean alive;
    public int ssid;

    public Record (int runId, DeathTask deathTask, int ssid, boolean alive) {
        this.runId = runId;
        this.deathTask = deathTask;
        this.ssid = ssid;
        this.alive = alive;
    }
}