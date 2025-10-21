import React, { useState, useEffect } from 'react';
import { getNotifications, markNotificationAsRead } from '../../api';

const NotificationList = () => {
    const [notifications, setNotifications] = useState([]);

    const fetchNotifications = async () => {
        try {
            const { data } = await getNotifications();
            setNotifications(data);
        } catch (error) {
            console.error("Failed to fetch notifications", error);
        }
    };

    useEffect(() => {
        fetchNotifications();
    }, []);

    const handleMarkAsRead = async (id) => {
        try {
            await markNotificationAsRead(id);
            fetchNotifications(); // Refresh list
        } catch (error) {
            console.error("Failed to mark notification as read", error);
        }
    };

    return (
        <div className="max-w-2xl mx-auto bg-white p-8 rounded-lg shadow-md">
            <h2 className="text-3xl font-bold text-gray-800 mb-6">Notifications</h2>
            <div className="space-y-4">
                {notifications.length > 0 ? (
                    notifications.map(notification => (
                        <div key={notification.id} className={`p-4 border rounded-md flex justify-between items-center ${notification.isRead ? 'bg-gray-100' : 'bg-white'}`}>
                            <p className={notification.isRead ? 'text-gray-500' : 'text-gray-800'}>{notification.message}</p>
                            {!notification.isRead && (
                                <button onClick={() => handleMarkAsRead(notification.id)} className="text-sm text-blue-500 hover:underline">
                                    Mark as read
                                </button>
                            )}
                        </div>
                    ))
                ) : (
                    <p>No notifications.</p>
                )}
            </div>
        </div>
    );
};

export default NotificationList;