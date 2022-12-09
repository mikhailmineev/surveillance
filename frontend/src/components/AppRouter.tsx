import * as React from 'react'
import { HashRouter, Route, Routes } from 'react-router-dom'

import HomePage from '../pages/HomePage'
import ActuatorPage from "../pages/ActuatorPage";
import ConfigurePage from '../pages/ConfigurePage'

export default () => {
    return (
        <HashRouter>
            <Routes>
                <Route path="/configure" element={<ConfigurePage />} />
                <Route path="/actuatorui" element={<ActuatorPage />} />
                <Route path="/" element={<HomePage />} />
            </Routes>
        </HashRouter>
    )
}
