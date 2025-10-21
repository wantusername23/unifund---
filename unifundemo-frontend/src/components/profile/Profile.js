import React, { useState, useEffect } from 'react';
import { getProfile, updateProfile } from '../../api';

const Profile = () => {
    const [profile, setProfile] = useState(null);
    const [isEditing, setIsEditing] = useState(false);
    const [formData, setFormData] = useState({ nickname: '', bio: '', socialMediaLink: '' });

    useEffect(() => {
        const fetchProfile = async () => {
            try {
                const { data } = await getProfile();
                setProfile(data);
                setFormData({
                    nickname: data.nickname,
                    bio: data.bio || '',
                    socialMediaLink: data.socialMediaLink || '',
                });
            } catch (error) {
                console.error("Failed to fetch profile", error);
            }
        };
        fetchProfile();
    }, []);

    const handleUpdate = async (e) => {
        e.preventDefault();
        try {
            const { data } = await updateProfile(formData);
            setProfile(data);
            setIsEditing(false);
        } catch (error) {
            console.error("Failed to update profile", error);
        }
    };

    if (!profile) {
        return <div className="text-center mt-10">Loading profile...</div>;
    }

    return (
        <div className="max-w-2xl mx-auto bg-white p-8 rounded-lg shadow-md">
            <h2 className="text-3xl font-bold text-gray-800 mb-6">My Profile</h2>
            {isEditing ? (
                <form onSubmit={handleUpdate} className="space-y-4">
                    <div>
                        <label className="block text-sm font-medium text-gray-700">Nickname</label>
                        <input
                            type="text"
                            value={formData.nickname}
                            onChange={(e) => setFormData({ ...formData, nickname: e.target.value })}
                            className="w-full px-4 py-2 mt-1 border rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                        />
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-700">Bio</label>
                        <textarea
                            value={formData.bio}
                            onChange={(e) => setFormData({ ...formData, bio: e.target.value })}
                            className="w-full px-4 py-2 mt-1 border rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                            rows="3"
                        />
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-700">Social Media Link</label>
                        <input
                            type="text"
                            value={formData.socialMediaLink}
                            onChange={(e) => setFormData({ ...formData, socialMediaLink: e.target.value })}
                            className="w-full px-4 py-2 mt-1 border rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                        />
                    </div>
                    <div className="flex space-x-4">
                        <button type="submit" className="w-full py-2 px-4 font-semibold text-white bg-blue-500 rounded-md hover:bg-blue-600 transition duration-300">Save Changes</button>
                        <button type="button" onClick={() => setIsEditing(false)} className="w-full py-2 px-4 font-semibold text-gray-700 bg-gray-200 rounded-md hover:bg-gray-300 transition duration-300">Cancel</button>
                    </div>
                </form>
            ) : (
                <div className="space-y-4">
                    <p><strong>Nickname:</strong> {profile.nickname}</p>
                    <p><strong>Bio:</strong> {profile.bio || 'Not set'}</p>
                    <p><strong>Social Media:</strong> {profile.socialMediaLink || 'Not set'}</p>
                    <button onClick={() => setIsEditing(true)} className="w-full py-2 px-4 font-semibold text-white bg-green-500 rounded-md hover:bg-green-600 transition duration-300">Edit Profile</button>
                </div>
            )}
        </div>
    );
};

export default Profile;