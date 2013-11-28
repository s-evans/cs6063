public class Record {
    public int runId;
    public DeathTask deathTask;
    public boolean alive;
    public int consensusValue;
    public int ssid;

    // TODO:
    // Send consensus values regularly
    // Special handling for the leader's consensus value
    // Handle consensus messages
    // Need a state machine for consensus decided or not (?)
    // UI improvements
    // Toggle byzantine failure (via UI?)
    // Toggle partition failure (via UI?)

    public Record (int runId, DeathTask deathTask, int ssid, boolean alive, int consensusValue) {
        this.runId = runId;
        this.deathTask = deathTask;
        this.ssid = ssid;
        this.alive = alive;
        this.consensusValue = consensusValue;
    }
}