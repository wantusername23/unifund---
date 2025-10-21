import React, { useState, useEffect } from 'react';
import { getCharacters, createCharacter } from '../../api';

const CharacterList = ({ worldviewId, isCreator }) => {
    const [characters, setCharacters] = useState([]);
    const [name, setName] = useState('');
    const [description, setDescription] = useState('');
    const [relationships, setRelationships] = useState('');

    const fetchCharacters = async () => {
        try {
            const response = await getCharacters(worldviewId);
            setCharacters(response.data);
        } catch (error) {
            console.error("Failed to fetch characters", error);
        }
    };

    useEffect(() => {
        fetchCharacters();
    }, [worldviewId]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await createCharacter(worldviewId, { name, description, relationships });
            setName('');
            setDescription('');
            setRelationships('');
            fetchCharacters(); // 목록 새로고침
        } catch (error) {
            alert('Failed to create character. Only creators or editors can add characters.');
        }
    };

    return (
        <div className="bg-white p-8 rounded-lg shadow-md mt-6">
            <h3 className="text-2xl font-bold text-gray-800 mb-4">Characters</h3>

            {isCreator && (
                <form onSubmit={handleSubmit} className="mb-6 p-4 border rounded-md bg-gray-50 space-y-4">
                    <h4 className="text-lg font-semibold">Add New Character</h4>
                    <div>
                        <label className="block text-sm font-medium text-gray-700">Name</label>
                        <input type="text" value={name} onChange={e => setName(e.target.value)} className="w-full px-4 py-2 mt-1 border rounded-md" required />
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-700">Description</label>
                        <textarea value={description} onChange={e => setDescription(e.target.value)} className="w-full px-4 py-2 mt-1 border rounded-md" rows="3" required />
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-700">Relationships</label>
                        <textarea value={relationships} onChange={e => setRelationships(e.target.value)} className="w-full px-4 py-2 mt-1 border rounded-md" rows="2" />
                    </div>
                    <button type="submit" className="px-4 py-2 font-semibold text-white bg-blue-500 rounded-md hover:bg-blue-600">Add Character</button>
                </form>
            )}

            <div className="space-y-4">
                {characters.map(char => (
                    <div key={char.id} className="p-4 border rounded-md">
                        <h4 className="text-xl font-semibold">{char.name}</h4>
                        <p className="text-gray-700 mt-2">{char.description}</p>
                        <p className="text-gray-500 text-sm mt-2"><strong>Relationships:</strong> {char.relationships}</p>
                    </div>
                ))}
            </div>
        </div>
    );
};

export default CharacterList;