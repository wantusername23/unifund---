import React, { useState, useEffect } from 'react';
import { getMembershipTiers, subscribeToMembership } from '../../api';

const Memberships = ({ worldviewId }) => {
    const [tiers, setTiers] = useState([]);

    const fetchTiers = async () => {
        try {
            const { data } = await getMembershipTiers(worldviewId);
            setTiers(data);
        } catch (error) {
            console.error("Failed to fetch membership tiers", error);
        }
    };

    useEffect(() => {
        fetchTiers();
    }, [worldviewId]);

    const handleSubscribe = async (tierId) => {
        try {
            await subscribeToMembership(tierId);
            alert('Subscription successful!');
        } catch (error) {
            alert('Failed to subscribe. Are you logged in?');
        }
    };

    if (tiers.length === 0) return null;

    return (
        <div className="bg-white p-6 rounded-lg shadow-md mb-6">
            <h3 className="text-2xl font-bold text-gray-800 mb-4">Join Membership</h3>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                {tiers.map(tier => (
                    <div key={tier.id} className="border p-6 rounded-lg flex flex-col justify-between">
                        <div>
                            <h4 className="text-xl font-semibold">{tier.name} (Level {tier.level})</h4>
                            <p className="text-3xl font-bold my-4">${tier.price}</p>
                            <p className="text-gray-600">{tier.description}</p>
                        </div>
                        <button
                            onClick={() => handleSubscribe(tier.id)}
                            className="mt-6 w-full py-2 px-4 font-semibold text-white bg-green-500 rounded-md hover:bg-green-600 transition duration-300"
                        >
                            Subscribe
                        </button>
                    </div>
                ))}
            </div>
        </div>
    );
};

export default Memberships;