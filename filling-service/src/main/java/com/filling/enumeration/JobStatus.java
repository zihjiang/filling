package com.filling.enumeration;

public enum JobStatus {

    Created(1, "Created"),
    Running(2, "Running"),
    Finished(3, "Finished"),
    Failed(4, "Failed"),
    Canceled(5, "Canceled"),
    Failing(6, "Failing"),
    Canceling(7, "Canceling"),
    Restarting(8, "Restarting");


    JobStatus(int i, String Created) {
    }
}
