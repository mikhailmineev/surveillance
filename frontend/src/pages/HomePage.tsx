import * as React from 'react'
import {useEffect, useState} from "react";
import {useKeycloak} from "@react-keycloak/web";

export default () => {
    const [streams, setStreams] = useState<string[]>([])
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
        }
        fetchData()
    }, [])

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
                            <video poster={`/stream/thumb/$entry/thumb.jpg`} controls>
                                <source src={"/stream/webm/$entry/stream.webm"} type="video/webm" />
                                <source src={"/stream/hls/$entry/stream.m3u8"} type="application/vnd.apple.mpegurl" />
                            </video>
                        </div>
                )
                })}
            </div>
            <h2>Old records</h2>
            <div id="records">

            </div>
        </div>
    );
};
