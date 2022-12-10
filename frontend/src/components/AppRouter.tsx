import * as React from 'react'
import {BrowserRouter, Route, Routes} from 'react-router-dom'

import HomePage from '../pages/HomePage'
import ActuatorPage from "../pages/ActuatorPage";
import ConfigurePage from '../pages/ConfigurePage'
import Header from "./Header";
import { ProtectedRoute } from "./ProtectedRoute";

export default () => {
    return (
        <BrowserRouter>
            <Header />
            <Routes>
                <Route path="/configure" element={
                    <ProtectedRoute>
                        <ConfigurePage />
                    </ProtectedRoute>} />
                <Route path="/actuator" element={
                    <ProtectedRoute>
                        <ActuatorPage />
                    </ProtectedRoute>} />
                <Route path="/" element={
                    <ProtectedRoute>
                        <HomePage />
                    </ProtectedRoute>} />
            </Routes>
        </BrowserRouter>
    )
}
