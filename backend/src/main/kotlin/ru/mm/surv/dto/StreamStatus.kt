package ru.mm.surv.dto

enum class StreamStatus {
    STARTING,
    RUNNING,
    STOPPING,
    STOPPED;

}

/**
 * There can be multiple stream sources with different statuses.
 * The main idea is if anything is running, we assume stream is running.
 * Stream is stopped only when all streams are stopped.
 *
 * When there are no any statuses, we assume stream is stopped
 *
 * The matrix from below shows, how aggregated status for two streams is calculated:
 *
 * | Stream A \ Stream B | STARTING | RUNNING  | STOPPING | STOPPED  |
 * | ------------------- | -------- | -------- | -------- | -------- |
 * | STARTING            | STARTING | STARTING | STARTING | STARTING |
 * | RUNNING             |          | RUNNING  | RUNNING  | RUNNING  |
 * | STOPPING            |          |          | STOPPING | STOPPING |
 * | STOPPED             |          |          |          | STOPPED  |
 */
fun aggregatedStreamStatus(statuses: Collection<StreamStatus>): StreamStatus {
    return statuses.sorted().getOrElse(0) { StreamStatus.STOPPED }
}
