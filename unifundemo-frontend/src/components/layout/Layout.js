import React from 'react';
import { Link, useNavigate } from 'react-router-dom';

const Layout = ({ children }) => {
    const token = localStorage.getItem('token');
    const navigate = useNavigate();

    const handleLogout = () => {
        localStorage.removeItem('token');
        window.location.href = '/login'; // 새로고침을 위해 navigate 대신 사용
    };

    return (
        <div className="bg-gray-100 min-h-screen font-sans">
            {/* 네비게이션 바 */}
            <header className="bg-white shadow-md sticky top-0 z-50">
                <nav className="container mx-auto px-6 py-4 flex justify-between items-center">
                    <Link to="/" className="text-2xl font-bold text-gray-800 hover:text-blue-500">
                        UniFundemo
                    </Link>
                    <div className="flex items-center space-x-4">
                        <Link to="/popular" className="text-gray-600 hover:text-blue-500">Popular</Link>
                        {token ? (
                            <>
                                <Link to="/profile" className="text-gray-600 hover:text-blue-500">Profile</Link>
                                <Link to="/bookmarks" className="text-gray-600 hover:text-blue-500">Bookmarks</Link>
                                <Link to="/notifications" className="text-gray-600 hover:text-blue-500">Notifications</Link>
                                <Link to="/create-worldview" className="bg-blue-500 hover:bg-blue-600 text-white font-bold py-2 px-4 rounded-full transition duration-300">
                                    Create Worldview
                                </Link>
                                <button
                                    onClick={handleLogout}
                                    className="bg-red-500 hover:bg-red-600 text-white font-bold py-2 px-4 rounded-full transition duration-300"
                                >
                                    Logout
                                </button>
                            </>
                        ) : (
                            <>
                                <Link to="/login" className="text-gray-600 hover:text-blue-500">Login</Link>
                                <Link to="/signup" className="bg-green-500 hover:bg-green-600 text-white font-bold py-2 px-4 rounded-full transition duration-300">
                                    Sign Up
                                </Link>
                            </>
                        )}
                    </div>
                </nav>
            </header>

            {/* 메인 컨텐츠 영역 */}
            <main className="container mx-auto px-6 py-8">
                {children}
            </main>

            {/* 푸터 */}
            <footer className="bg-white mt-8 py-4 text-center text-gray-500 text-sm">
                <p>&copy; 2025 UniFundemo. All rights reserved.</p>
            </footer>
        </div>
    );
};

export default Layout;