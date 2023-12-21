import React, {useEffect, useState} from 'react';
import {Link, useNavigate} from 'react-router-dom';
import Auth from '../../backend/Auth';

export default function Header() {
    const [authenticated, setAuthenticated] = useState(Auth.isTokenValid);
    const username = Auth.getUsernameFromToken();
    const navigate = useNavigate();


    const handleLogoutAndNavigate = () => {
        setAuthenticated(Auth.logout());
        navigate('/');
    };

    useEffect(() => {
        const intervalId = setInterval(() => {
            setAuthenticated(Auth.isTokenValid());
        }, 1000);

        return () => {
            clearInterval(intervalId);
        };
    }, []);

    return (
        <div>
            <div className="container head">
                <div className="header">
                    <Link to={'/'}>
                    </Link>
                    <div className="header__title">ФОРУМ</div>
                    <div className="header__nav">
                        {authenticated ? (
                            <div className="display__flex align__items">
                                <button
                                    type="button"
                                    className="nav__img"
                                    onClick={handleLogoutAndNavigate}
                                >
                                </button>
                            </div>
                        ) : (
                            <div>
                                <Link className="header__nav__text" to={'/auth'}>
                                    <img alt=""/>Войти
                                </Link>
                            </div>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
}
