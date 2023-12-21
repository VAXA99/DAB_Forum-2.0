import React, {useEffect, useState} from 'react';
import {Link, useNavigate} from 'react-router-dom';
import auth from '../../backend/Auth'

export default function SignUp() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');

    const [usernameError, setUsernameError] = useState('');
    const [emptyCredentialsError, setEmptyCredentialsError] = useState('');
    const [someError, setSomeError] = useState('');

    const navigate = useNavigate();


    const handlePasswordChange = (e) => {
        const newPassword = e.target.value;
        setPassword(newPassword);
    };


    const handleSignUp = async (e) => {
        e.preventDefault();

        const isValidUsername = await auth.isUsernameValid(username);

        setUsernameError('');
        setEmptyCredentialsError('');
        setSomeError('');

        if (!username || !password) {
            setEmptyCredentialsError("All fields must be filled");
            return;
        }

        if (!isValidUsername) {
            setUsernameError("Данный логин уже занят")
        }

        if (!isValidUsername) {
            return;
        }

        const {success, error} = await auth.signUp(username, password);
        if (success) {
            navigate("/auth");
        } else {
            setSomeError(error);
        }
    };

    const goBack = () => {
        navigate(-1);
    };

    useEffect(() => {
        const tokenValid = auth.isTokenValid();
        if (tokenValid) {
            navigate("/auth")
        }
    }, [navigate]);

    return (
        <div className='body'>
            <div className='body'>
                <link href="https://fonts.cdnfonts.com/css/sf-pro-display" rel="stylesheet"/>
                <link href="https://fonts.cdnfonts.com/css/forma-djr-banner" rel="stylesheet"/>
                <link href="https://fonts.googleapis.com/css2?family=Inter+Tight:wght@900&display=swap"
                      rel="stylesheet"></link>

                <div className="container auth">
                    <Link to={"/"}>
                        <div className="auth__title">ФОРУМ</div>
                    </Link>
                    <form onSubmit={handleSignUp}>
                        <div className="auth__form">
                            <div className="input__block">
                                <div>
                                    <input
                                        className="auth__input"
                                        placeholder='логин'
                                        type="text"
                                        value={username}
                                        onChange={(e) => setUsername(e.target.value)}
                                    />
                                    {usernameError && <div className="auth__error sign__up">{usernameError}</div>}
                                </div>
                                <div>
                                    <input
                                        className="auth__input"
                                        placeholder='пароль'
                                        type="password"
                                        value={password}
                                        onChange={handlePasswordChange}
                                    />
                                </div>
                            </div>
                            <div className="form__buttons">
                                <button type="submit" className="auth__button">
                                    Регистрация
                                </button>
                                <div className="auth__link" onClick={goBack}>Назад</div>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </div>)
}

