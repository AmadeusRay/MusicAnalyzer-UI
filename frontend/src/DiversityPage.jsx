import React, { useState } from 'react';

function DiversityPage() {
  const [username, setUsername] = useState('');
  const [results, setResults] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setResults('');
    try {
      const response = await fetch(`/api/diversity?user=${encodeURIComponent(username)}`);
      const text = await response.text();
      setResults(text);
    } catch (err) {
      setResults('Error fetching results.');
    }
    setLoading(false);
  };

  return (
    <div style={{ maxWidth: 600, margin: 'auto', padding: 32 }}>
      <h1>Music Diversity Analyzer</h1>
      <form onSubmit={handleSubmit}>
        <label>
          Username:
          <input
            type="text"
            value={username}
            onChange={e => setUsername(e.target.value)}
            required
            style={{ marginLeft: 8 }}
          />
        </label>
        <button type="submit" style={{ marginLeft: 16 }}>Login</button>
      </form>
      {loading && <p>Loading...</p>}
      {results && (
        <div style={{ marginTop: 32 }}>
          <div dangerouslySetInnerHTML={{ __html: results }} />
        </div>
      )}
    </div>
  );
}

export default DiversityPage; 