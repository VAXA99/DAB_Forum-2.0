import React, { useEffect, useState } from 'react';
import { Navigate, Route } from 'react-router-dom';
import auth from "./backend/Auth";
const TOKEN_CHECK_INTERVAL = 1000;

const ProtectedRoute = ({ element }) => {
    const [authenticated, setAuthenticated] = useState(auth.isTokenValid());

    useEffect(() => {
        const checkAuthentication = async () => {
            const isAuthenticated = await auth.isTokenValid();
            setAuthenticated(isAuthenticated);
        };

        checkAuthentication();

        const tokenCheckInterval = setInterval(checkAuthentication, TOKEN_CHECK_INTERVAL);

        return () => clearInterval(tokenCheckInterval);
    }, []);

    return authenticated ? element : <Navigate to="/auth" />;
};

export default ProtectedRoute;
