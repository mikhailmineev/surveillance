import * as React from 'react'
import {BrowserRouter, Route, Routes} from 'react-router-dom'

import HomePage from '../pages/HomePage'
import ActuatorPage from "../pages/ActuatorPage";
import ConfigurePage from '../pages/ConfigurePage'
import Header from "./Header";

export default () => {
    return (
        <BrowserRouter>
            <Header />
            <Routes>
                <Route path="/configure" element={<ConfigurePage />} />
                <Route path="/actuator" element={<ActuatorPage />} />
                <Route path="/" element={<HomePage />} />
            </Routes>
        </BrowserRouter>
    )
}
