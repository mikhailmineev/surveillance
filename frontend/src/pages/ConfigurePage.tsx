import * as React from 'react'
import {useEffect, useState} from "react";
import {SystemConfig} from "../types/types";
import {useKeycloak} from "@react-keycloak/web";

export default () => {
    const [config, setConfig] = useState<SystemConfig>({recorders:[], inputSources:[]})
    const { keycloak } = useKeycloak();

    useEffect(() => {
        const fetchData = async () => {
            let rawConfigData = await fetch("/api/config", {
                headers: {
                    "Authorization": "Bearer " + keycloak.token
                }
            })
            let configData = await rawConfigData.json()
            setConfig(configData)
        }
        fetchData()
    }, [])

    return (
        <div className="container">
            <h1>Configuration</h1>
            <h2>Active</h2>
            <h3>Manage</h3>
            <div className="container p-0">
                { config.recorders.map(recorder => {
                    return (
                        <div className="row">
                            <div className="col-sm-auto flex-fill">
                                <label className="form-label w-100">
                                    Name
                                    <input className="form-control" type="text" value={recorder.name} disabled />
                                </label>
                            </div>
                            <div className="col-sm-auto flex-fill">
                                <label className="form-label w-100">
                                    Video
                                    <input className="form-control" type="text" value={"recorder.video"} disabled/>
                                </label>
                            </div>
                            <div className="col-sm-auto flex-fill">
                                <label className="form-label w-100">
                                    Input framerate
                                    <input className="form-control" type="text" value={recorder.inputFramerate} disabled/>
                                </label>
                            </div>
                            <div className="col-sm-auto flex-fill">
                                <label className="form-label w-100">
                                    Input resolution
                                    <input className="form-control" type="text" value={recorder.inputResolution} disabled/>
                                </label>
                            </div>
                            <div className="col-sm-auto flex-fill">
                                <label className="form-label w-100">
                                    Audio
                                    <input className="form-control" type="text" value={recorder.audio} disabled/>
                                </label>
                            </div>
                            <div className="col-sm-auto flex-fill">
                                <button className="btn btn-primary" disabled>Update</button>
                            </div>
                            <div className="col-sm-auto flex-fill">
                                <button className="btn btn-danger" disabled>Delete</button>
                            </div>
                        </div>
                    )
                })}
            </div>
            <h3>Add local source</h3>
            <form action="/stream/control/input" method="post">
                <table>
                    <tr>
                        <td>
                            <label>Name <input name="name" type="text"/></label>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <label>Video <input name="video" type="text"/></label>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <label>Input framerate <input name="inputFramerate" type="text"/></label>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <label>Input resolution <input name="inputResolution" type="text"/></label>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <label>Audio <input name="audio" type="text"/></label>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <button className="btn btn-lg btn-primary btn-block" type="submit">Add</button>
                        </td>
                    </tr>
                </table>
            </form>
            <h3>Add remote source</h3>
            <h2>Supported captures</h2>
            <table className="table">
                <tr>
                    <th>Id</th>
                    <th>Type</th>
                    <th>Name</th>
                    <th>Format</th>
                </tr>
                { config.inputSources.map(inputSource => {
                    return (
                        <tr>
                            <td>{inputSource.id}</td>
                            <td>{inputSource.type}</td>
                            <td>{inputSource.name}</td>
                            <td>
                                { inputSource.formats.map(inputFormat => {
                                    return (
                                        <div>
                                            <span>{inputFormat.resolution}</span>, fps=<span>{inputFormat.fps}</span>
                                        </div>
                                    )
                                })}
                            </td>
                        </tr>
                    )
                })}
            </table>
        </div>
    )
}
