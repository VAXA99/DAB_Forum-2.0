import "./components/SignIn/sign_in.css";

import React from "react";
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import ProtectedRoute from "./ProtectedRoute";
import SignUp from "./components/SignUp/SignUp";
import Header from "./components/Header/Header";
import { SignIn } from "./components/SignIn/SignIn";

function App() {
    const shouldRenderHeader = () => {
        const currentPath = window.location.pathname;
        return !(currentPath === "/auth" || currentPath === "/sign_up");
    };

    return (
        <Router>
            <div>
                {shouldRenderHeader() && <Header />}

                <Routes>
                    <Route path="/auth" element={<SignIn />} />
                    <Route path="/sign_up" element={<SignUp />} />
                </Routes>
            </div>
        </Router>
    );
}

export default App;
