import React, { useState, useEffect } from 'react';
import { getContributors } from '../../api';

const Contributors = ({ worldviewId }) => {
    const [contributors, setContributors] = useState([]);

    useEffect(() => {
        const fetchContributors = async () => {
            try {
                const { data } = await getContributors(worldviewId);
                setContributors(data);
            } catch (error) {
                console.error("Failed to fetch contributors", error);
            }
        };
        fetchContributors();
    }, [worldviewId]);

    return (
        <div className="bg-white p-8 rounded-lg shadow-md mt-6">
            <h3 className="text-2xl font-bold text-gray-800 mb-4">Contributors</h3>
            <div className="space-y-4">
                {contributors.map(con => (
                    <div key={con.userId} className="p-4 border rounded-md flex justify-between items-center">
                        <span className="font-semibold">{con.nickname}</span>
                        <span className="text-sm bg-gray-200 text-gray-700 px-3 py-1 rounded-full">{con.permission}</span>
                    </div>
                ))}
            </div>
        </div>
    );
};

export default Contributors;