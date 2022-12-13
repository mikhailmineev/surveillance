import * as React from 'react'
import {useEffect, useState} from "react";
import {useKeycloak} from "@react-keycloak/web";
import {StreamRecord, StreamRecordVideo} from "../types/types";

export default () => {
    const [streams, setStreams] = useState<string[]>([])
    const [records, setRecords] = useState<StreamRecord[]>([])
    const { keycloak } = useKeycloak();

    useEffect(() => {
        const fetchData = async () => {
            let rawStreamData = await fetch("/api/stream", {
                headers: {
                    "Authorization": "Bearer " + keycloak.token
                }
            })
            let streamData = await rawStreamData.json()
            setStreams(streamData)

            let rawRecordsData = await fetch("/api/record/mp4", {
                headers: {
                    "Authorization": "Bearer " + keycloak.token
                }
            })
            let recordsData = await rawRecordsData.json()
            setRecords(recordsData)
        }
        fetchData()
    }, [])
    const videoStyle = {
        width: "100%",
        maxWidth: "500px"
    }
    const requestDeleteVideo = (video : StreamRecordVideo) => {
        if (window.confirm(`Delete video ${video.name}?`)) {
            deleteVideo(video);
        }
    }
    const deleteVideo = async (video: StreamRecordVideo) => {
        await fetch(`/api/record/mp4/${video.name}/record.mp4`, {
            method: 'DELETE',
            headers: {
                "Authorization": "Bearer " + keycloak.token
            }
        })

        let recordsCopy: StreamRecord[] = []
        records.forEach(record => {
            let recordVideosCopy = record.videos.filter(e => e !== video)
            if (recordVideosCopy.length > 0) {
                recordsCopy.push({date: record.date, videos: recordVideosCopy});
            }
        })

        setRecords(recordsCopy)
    }

    return (
        <div className="container">
            <h1>Streams</h1>
            <h2>Running streams</h2>
            <div id="streams" className="row">
                { (streams?.length === 0 ?? false) &&
                    <p className="text-muted">No streams running</p>
                }
                { streams.map(entry => {
                    return (
                        <div className="col-md">
                            <h3>Camera <span>{entry}</span></h3>
                            <video poster={`/api/stream/thumb/${entry}/thumb.jpg?access_token=${keycloak.token}`} controls style={videoStyle}>
                                <source src={`/api/stream/webm/${entry}/stream.webm?access_token=${keycloak.token}`} type="video/webm" />
                                <source src={`/api/stream/hls/${entry}/stream.m3u8?access_token=${keycloak.token}`} type="application/vnd.apple.mpegurl" />
                            </video>
                        </div>
                    )
                })}
            </div>
            <h2>Old records</h2>
            <div id="records">
                { records.map(entry => {
                    return (
                        <div id={`recordframe-${entry.date}`}>
                            <h3>Record of <span>{entry.date}</span></h3>
                            <div className="row" id={`videogroup-${entry.date}`}>
                                { entry.videos.map(video => {
                                    return (
                                        <div className="col-md" id={`videoelement-${video.name}`}>
                                            <h4>Camera <span>{video.cameraId}</span>
                                                <button onClick={() => { requestDeleteVideo(video) }}>Delete</button>
                                            </h4>
                                            <video poster={`/api/record/mp4/${video.name}/thumb.jpg?access_token=${keycloak.token}`} controls style={videoStyle} preload="none">
                                                <source src={`/api/record/mp4/${video.name}/record.mp4?access_token=${keycloak.token}`} type="video/mp4" />
                                            </video>
                                        </div>
                                    )
                                })}
                                </div>
                        </div>
                    )
                })}
            </div>
        </div>
    )
}
